package com.ims.controller;

import com.ims.dto.request.AuthRequest;
import com.ims.dto.response.ApiResponse;
import com.ims.dto.response.AuthResponse;
import com.ims.dto.response.UserResponse;
import com.ims.entity.User;
import com.ims.service.impl.AuthService;
import com.ims.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest.Login request) {
        return ResponseEntity.ok(
                ApiResponse.success("Đăng nhập thành công", authService.login(request)));
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody AuthRequest.Register request) {
        return ResponseEntity.ok(
                ApiResponse.success("Đăng ký thành công", authService.register(request)));
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody AuthRequest.RefreshToken request) {
        return ResponseEntity.ok(
                ApiResponse.success(authService.refreshToken(request.getRefreshToken())));
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody AuthRequest.RefreshToken request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }

    // GET /api/auth/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.toResponse(user)));
    }
}
