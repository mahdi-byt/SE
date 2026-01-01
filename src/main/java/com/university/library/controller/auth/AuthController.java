package com.university.library.controller.auth;

import com.university.library.dto.auth.ChangePasswordRequest;
import com.university.library.dto.auth.LoginRequest;
import com.university.library.dto.auth.RegisterRequest;
import com.university.library.model.User;
import com.university.library.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.university.library.dto.ApiResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 🔹 ثبت‌نام دانشجو
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        authService.register(request.getUsername(), request.getPassword());

        return ResponseEntity.ok().body(
                new ApiResponse(true, "Student registered successfully")
        );
    }

    // 🔹 ورود به سیستم
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok().body(
                new ApiResponse(true, "Login successful", user)
        );
    }

    // 🔹 تغییر رمز عبور (کارمند / مدیر)
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("username") String username,
            @RequestBody ChangePasswordRequest request) {

        authService.changePassword(
                username,
                request.getOldPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok().body(
                new ApiResponse(true, "Password changed successfully")
        );
    }
}