package com.microservices.authservice.controller;

import com.microservices.authservice.dto.LoginRequest;
import com.microservices.authservice.dto.LoginResponse;
import com.microservices.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Authentication APIs",
description = "APIs related to user authentication")
@RestController
@RequestMapping("/auth")
public class AuthController
{
    @Autowired
    private AuthService authService;

    @Operation(summary = "User Login",
    description = "Authenticates user and returns JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid  @RequestBody LoginRequest request)
    {
        log.info("Login request received for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        log.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Internal API - Checks user existence",
            description = "Checks if user exist for userId")
    @GetMapping("/users/{userId}/exists")
    public ResponseEntity<Boolean> isUserExists(@PathVariable Long userId)
    {
        return ResponseEntity.ok(authService.isUserExists(userId));
    }
}
