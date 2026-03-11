package com.ims.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    private String customerPhone;
    private String customerEmail;
    private String note;

    @NotEmpty(message = "Đơn hàng phải có ít nhất 1 sản phẩm")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product ID không được để trống")
        private Long productId;

        @NotNull
        @Min(value = 1, message = "Số lượng phải >= 1")
        private Integer quantity;
    }
}
