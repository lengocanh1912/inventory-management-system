package com.ims.dto.request;

import com.ims.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderUpdateStatusRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private OrderStatus status;
}
