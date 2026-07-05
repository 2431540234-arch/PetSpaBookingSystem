package com.petspa.backend.controller;

import com.petspa.backend.dto.request.LoginRequest;
import com.petspa.backend.dto.response.AuthResponse;
import com.petspa.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Khop dung "POST auth/login" trong ApiService.kt (baseUrl + context-path /api se ra /api/auth/login)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
