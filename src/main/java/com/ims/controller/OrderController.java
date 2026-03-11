package com.ims.controller;

import com.ims.dto.request.OrderRequest;
import com.ims.dto.request.OrderUpdateStatusRequest;
import com.ims.dto.response.*;
import com.ims.enums.OrderStatus;
import com.ims.service.impl.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // GET /api/orders?search=&status=&page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.getAll(search, status, page, size)));
    }

    // GET /api/orders/:id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getById(id)));
    }

    // POST /api/orders
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo đơn hàng thành công", orderService.create(request)));
    }

    // PATCH /api/orders/:id/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateStatusRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật trạng thái thành công",
                        orderService.updateStatus(id, request)));
    }
}
