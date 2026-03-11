package com.ims.service.impl;

import com.ims.dto.request.InventoryAdjustRequest;
import com.ims.dto.response.MovementResponse;
import com.ims.dto.response.PageResponse;
import com.ims.dto.response.ProductResponse;
import com.ims.entity.InventoryMovement;
import com.ims.entity.Product;
import com.ims.entity.User;
import com.ims.enums.MovementType;
import com.ims.exception.BusinessException;
import com.ims.repository.InventoryMovementRepository;
import com.ims.repository.UserRepository;
import com.ims.security.SecurityUtils;
import com.ims.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryMovementRepository movementRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    public PageResponse<ProductResponse> getInventory(int page, int size) {
        // Returns products with their stock info
        var pageable = PageRequest.of(page, size);
        return productService.getAll(null, null, null, page, size);
    }

    public List<ProductResponse> getLowStock() {
        return productService.getLowStock();
    }

    @Transactional
    public MovementResponse adjustStock(Long productId,
                                        InventoryAdjustRequest request) {
        Product product = productService.findById(productId);

        int stockBefore = product.getStock();
        int delta = request.getType() == MovementType.IN
                ? request.getQuantity()
                : -request.getQuantity();
        int stockAfter = stockBefore + delta;

        if (stockAfter < 0) {
            throw new BusinessException(
                    "Không đủ hàng trong kho. Tồn kho hiện tại: " + stockBefore);
        }

        product.setStock(stockAfter);

        User currentUser = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow();

        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .type(request.getType())
                .quantity(request.getQuantity())
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .note(request.getNote())
                .performedBy(currentUser)
                .build();

        movement = movementRepository.save(movement);
        return toResponse(movement);
    }

    public PageResponse<MovementResponse> getMovements(Long productId, MovementType type,
                                                       int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PageResponse.from(
                movementRepository.findMovements(productId, type, pageable)
                        .map(this::toResponse));
    }

    private MovementResponse toResponse(InventoryMovement m) {
        return MovementResponse.builder()
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
                .build();
    }
}