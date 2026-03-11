package com.ims.service.impl;

import com.ims.dto.request.ProductRequest;
import com.ims.dto.request.ProductUpdateRequest;
import com.ims.dto.response.CategoryResponse;
import com.ims.dto.response.PageResponse;
import com.ims.dto.response.ProductResponse;
import com.ims.entity.Category;
import com.ims.entity.Product;
import com.ims.enums.ProductStatus;
import com.ims.exception.DuplicateResourceException;
import com.ims.exception.ResourceNotFoundException;
import com.ims.repository.CategoryRepository;
import com.ims.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // tất cả method mặc định read-only → giữ session mở khi đọc lazy
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public PageResponse<ProductResponse> getAll(String search, Long categoryId,
                                                ProductStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PageResponse.from(
                productRepository.searchProducts(search, categoryId, status, pageable)
                        .map(this::toResponse));
    }

    public ProductResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream().map(c ->
                CategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .build()
        ).toList();
    }

    public List<ProductResponse> getLowStock() {
        return productRepository.findLowStockProducts().stream()
                .map(this::toResponse).toList();
    }

    @Transactional  // override readOnly=true → write transaction
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU " + request.getSku() + " đã tồn tại");
        }
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        }
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .price(request.getPrice())
                .cost(request.getCost())
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .status(request.getStatus())
                .supplier(request.getSupplier())
                .image(request.getImage())
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = findById(id);
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCost() != null) product.setCost(request.getCost());
        if (request.getMinStock() != null) product.setMinStock(request.getMinStock());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getSupplier() != null) product.setSupplier(request.getSupplier());
        if (request.getImage() != null) product.setImage(request.getImage());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            product.setCategory(category);
        }
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        productRepository.deleteById(id);
    }

    // package-private để các service khác dùng (InventoryService, OrderService)
    Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    // Dùng Hibernate.isInitialized để tránh LazyInit khi category chưa load
    ProductResponse toResponse(Product p) {
        CategoryResponse cat = null;
        if (p.getCategory() != null) {
            try {
                cat = CategoryResponse.builder()
                        .id(p.getCategory().getId())
                        .name(p.getCategory().getName())
                        .build();
            } catch (Exception ignored) {
                // Lazy proxy chưa init → bỏ qua, trả null
            }
        }
        return ProductResponse.builder()
                .id(p.getId())
                .sku(p.getSku())
                .name(p.getName())
                .description(p.getDescription())
                .category(cat)
                .price(p.getPrice())
                .cost(p.getCost())
                .stock(p.getStock())
                .minStock(p.getMinStock())
                .status(p.getStatus())
                .supplier(p.getSupplier())
                .image(p.getImage())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}