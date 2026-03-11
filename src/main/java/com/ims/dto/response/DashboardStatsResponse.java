package com.ims.dto.response;
import com.ims.enums.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.Map;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardStatsResponse {
    private BigDecimal totalRevenue;
    private double revenueGrowth;
    private long totalOrders;
    private double ordersGrowth;
    private long totalProducts;
    private long lowStockCount;
    private long totalUsers;
    private Map<OrderStatus, Long> ordersByStatus;
}
