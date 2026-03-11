package com.ims.controller;

import com.ims.dto.response.*;
import com.ims.service.impl.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // GET /api/dashboard/stats
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getStats()));
    }

    // GET /api/dashboard/sales?period=7d
    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<List<SalesChartResponse>>> getSales(
            @RequestParam(defaultValue = "7d") String period) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSalesChart(period)));
    }

    // GET /api/dashboard/top-products
    @GetMapping("/top-products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getTopProducts() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getTopProducts()));
    }

    // GET /api/dashboard/activity
    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<List<MovementResponse>>> getActivity() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentActivity()));
    }
}
