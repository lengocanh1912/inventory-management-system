package com.ims.dto.response;
import com.ims.enums.ProductStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private CategoryResponse category;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer stock;
    private Integer minStock;
    private ProductStatus status;
    private String supplier;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
