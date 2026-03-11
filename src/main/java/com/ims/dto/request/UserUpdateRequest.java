package com.ims.dto.request;

import com.ims.enums.UserStatus;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private UserStatus status;
}
