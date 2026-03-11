package com.ims.controller;

import com.ims.dto.request.InventoryAdjustRequest;
import com.ims.dto.response.*;
import com.ims.enums.MovementType;
import com.ims.service.impl.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // GET /api/inventory?page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                ApiResponse.success(inventoryService.getInventory(page, size)));
    }

    // GET /api/inventory/low-stock
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getLowStock()));
    }

    // POST /api/inventory/:productId/adjust
    @PostMapping("/{productId}/adjust")
    public ResponseEntity<ApiResponse<MovementResponse>> adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryAdjustRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Điều chỉnh kho thành công",
                        inventoryService.adjustStock(productId, request)));
    }

    // GET /api/inventory/movements?productId=&type=&page=0&size=10
    @GetMapping("/movements")
    public ResponseEntity<ApiResponse<PageResponse<MovementResponse>>> getMovements(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) MovementType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                ApiResponse.success(inventoryService.getMovements(productId, type, page, size)));
    }
}
