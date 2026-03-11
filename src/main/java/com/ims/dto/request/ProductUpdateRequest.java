package com.ims.dto.request;

import com.ims.enums.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String name;
    private String description;
    private Long categoryId;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer minStock;
    private ProductStatus status;
    private String supplier;
    private String image;
}
