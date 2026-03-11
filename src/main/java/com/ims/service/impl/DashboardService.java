package com.ims.service.impl;

import com.ims.dto.response.DashboardStatsResponse;
import com.ims.dto.response.MovementResponse;
import com.ims.dto.response.ProductResponse;
import com.ims.dto.response.SalesChartResponse;
import com.ims.enums.OrderStatus;
import com.ims.repository.InventoryMovementRepository;
import com.ims.repository.OrderRepository;
import com.ims.repository.ProductRepository;
import com.ims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryMovementRepository movementRepository;
    private final ProductService productService;

    public DashboardStatsResponse getStats() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDateTime.now();
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusSeconds(1);

        BigDecimal revenueThisMonth = orderRepository.getRevenueBetween(startOfMonth, endOfMonth);
        BigDecimal revenueLastMonth = orderRepository.getRevenueBetween(startOfLastMonth, endOfLastMonth);
        long ordersThisMonth = orderRepository.countOrdersBetween(startOfMonth, endOfMonth);
        long ordersLastMonth = orderRepository.countOrdersBetween(startOfLastMonth, endOfLastMonth);

        double revenueGrowth = calcGrowth(revenueLastMonth.doubleValue(), revenueThisMonth.doubleValue());
        double ordersGrowth = calcGrowth(ordersLastMonth, ordersThisMonth);

        Map<OrderStatus, Long> ordersByStatus = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(status, orderRepository.countByStatus(status));
        }

        return DashboardStatsResponse.builder()
                .totalRevenue(orderRepository.getTotalCompletedRevenue())
                .revenueGrowth(revenueGrowth)
                .totalOrders(orderRepository.count())
                .ordersGrowth(ordersGrowth)
                .totalProducts(productRepository.count())
                .lowStockCount(productRepository.findLowStockProducts().size())
                .totalUsers(userRepository.count())
                .ordersByStatus(ordersByStatus)
                .build();
    }

    public List<SalesChartResponse> getSalesChart(String period) {
        int days = switch (period) {
            case "30d" -> 30;
            case "90d" -> 90;
            default -> 7;
        };
        return IntStream.range(0, days).mapToObj(i -> {
            LocalDate date = LocalDate.now().minusDays(days - 1 - i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            String label = days <= 7
                    ? date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("vi"))
                    : date.toString();
            return SalesChartResponse.builder()
                    .label(label)
                    .revenue(orderRepository.getRevenueBetween(start, end))
                    .orders(orderRepository.countOrdersBetween(start, end))
                    .build();
        }).toList();
    }

    public List<ProductResponse> getTopProducts() {
        // Dùng JOIN FETCH trong repository → category đã được load sẵn
        return productRepository.findTopByOrderByPriceDesc(PageRequest.of(0, 5))
                .stream().map(productService::toResponse).toList();
    }

    public List<MovementResponse> getRecentActivity() {
        return movementRepository.findTop10ByOrderByCreatedAtDesc(PageRequest.of(0, 10))
                .stream().map(m -> MovementResponse.builder()
                        .id(m.getId())
                        .productId(m.getProduct().getId())
                        .productName(m.getProduct().getName())
                        .productSku(m.getProduct().getSku())
                        .type(m.getType())
                        .quantity(m.getQuantity())
                        .stockBefore(m.getStockBefore())
                        .stockAfter(m.getStockAfter())
                        .note(m.getNote())
                        .performedBy(m.getPerformedBy() != null ? m.getPerformedBy().getName() : null)
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();
    }

    private double calcGrowth(double previous, double current) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return Math.round(((current - previous) / previous) * 1000.0) / 10.0;
    }
}