package com.dap.calculation.auth.controller;

import com.dap.calculation.auth.dto.SignupRequest;
import com.dap.calculation.auth.dto.LoginRequest;
import com.dap.calculation.auth.dto.LoginResponse;
import com.dap.calculation.auth.dto.RefreshTokenRequest;

import com.dap.calculation.auth.service.AuthService;


import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody SignupRequest request) {

        authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
    // REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        LoginResponse response = authService.refresh(request);

        return ResponseEntity.ok(response);
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authService.logout(request);

        return ResponseEntity.noContent().build();
    }
}