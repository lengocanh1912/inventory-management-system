package com.ims.dto.request;

import com.ims.enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "SKU không được để trống")
    private String sku;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;
    private Long categoryId;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal price;

    @NotNull(message = "Giá nhập không được để trống")
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal cost;

    @Min(0) private Integer stock = 0;
    @Min(0) private Integer minStock = 5;
    private ProductStatus status = ProductStatus.ACTIVE;
    private String supplier;
    private String image;
}
