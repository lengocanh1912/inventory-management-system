package com.ims.service.impl;

import com.ims.dto.request.OrderRequest;
import com.ims.dto.request.OrderUpdateStatusRequest;
import com.ims.dto.response.OrderItemResponse;
import com.ims.dto.response.OrderResponse;
import com.ims.dto.response.PageResponse;
import com.ims.entity.*;
import com.ims.enums.MovementType;
import com.ims.enums.OrderStatus;
import com.ims.exception.BusinessException;
import com.ims.exception.ResourceNotFoundException;
import com.ims.repository.InventoryMovementRepository;
import com.ims.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final InventoryMovementRepository movementRepository;

    public PageResponse<OrderResponse> getAll(String search, OrderStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PageResponse.from(
                orderRepository.searchOrders(search, status, pageable)
                        .map(o -> toResponse(o, false)));
    }

    public OrderResponse getById(Long id) {
        Order order = findById(id);
        return toResponse(order, true);
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())
                .note(request.getNote())
                .status(OrderStatus.PENDING)
                .staff(currentUser)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productService.findById(itemReq.getProductId());

            if (product.getStock() < itemReq.getQuantity()) {
                throw new BusinessException("Sản phẩm \"" + product.getName()
                        + "\" không đủ hàng. Tồn kho: " + product.getStock());
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(item);

            // Deduct stock
            int stockBefore = product.getStock();
            product.setStock(stockBefore - itemReq.getQuantity());

            movementRepository.save(InventoryMovement.builder()
                    .product(product)
                    .type(MovementType.OUT)
                    .quantity(itemReq.getQuantity())
                    .stockBefore(stockBefore)
                    .stockAfter(product.getStock())
                    .note("Xuất kho cho đơn hàng " + order.getOrderCode())
                    .performedBy(currentUser)
                    .build());
        }

        order.setTotal(total);
        return toResponse(orderRepository.save(order), true);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderUpdateStatusRequest request) {
        Order order = findById(id);
        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        // Validate transitions
        validateStatusTransition(oldStatus, newStatus);

        // If cancelled — restore stock
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            User currentUser = (User) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();

            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int stockBefore = product.getStock();
                product.setStock(stockBefore + item.getQuantity());

                movementRepository.save(InventoryMovement.builder()
                        .product(product)
                        .type(MovementType.IN)
                        .quantity(item.getQuantity())
                        .stockBefore(stockBefore)
                        .stockAfter(product.getStock())
                        .note("Hoàn kho do hủy đơn " + order.getOrderCode())
                        .performedBy(currentUser)
                        .build());
            }
        }

        order.setStatus(newStatus);
        return toResponse(orderRepository.save(order), true);
    }

    private void validateStatusTransition(OrderStatus from, OrderStatus to) {
        if (from == OrderStatus.COMPLETED || from == OrderStatus.CANCELLED) {
            throw new BusinessException("Không thể thay đổi trạng thái đơn hàng đã "
                    + (from == OrderStatus.COMPLETED ? "hoàn thành" : "hủy"));
        }
    }

    private Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    private String generateOrderCode() {
        String prefix = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        long count = orderRepository.count() + 1;
        return prefix + String.format("%04d", count);
    }

    private OrderResponse toResponse(Order order, boolean includeItems) {
        List<OrderItemResponse> items = null;
        if (includeItems && order.getItems() != null) {
            items = order.getItems().stream().map(i -> OrderItemResponse.builder()
                    .id(i.getId())
                    .productId(i.getProduct().getId())
                    .productName(i.getProductName())
                    .productSku(i.getProductSku())
                    .quantity(i.getQuantity())
                    .unitPrice(i.getUnitPrice())
                    .subtotal(i.getSubtotal())
                    .build()).toList();
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(order.getCustomerEmail())
                .status(order.getStatus())
                .total(order.getTotal())
                .note(order.getNote())
                .staffName(order.getStaff() != null ? order.getStaff().getName() : null)
                .itemCount(order.getItems() != null ? order.getItems().size() : 0)
                .items(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}