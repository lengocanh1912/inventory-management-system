package com.ims.dto.request;

import com.ims.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateRoleRequest {
    @NotNull(message = "Role không được để trống")
    private Role role;
}
