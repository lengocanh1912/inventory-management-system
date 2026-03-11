package com.ims.dto.response;
import com.ims.enums.Role;
import com.ims.enums.UserStatus;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;
    private String avatar;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
