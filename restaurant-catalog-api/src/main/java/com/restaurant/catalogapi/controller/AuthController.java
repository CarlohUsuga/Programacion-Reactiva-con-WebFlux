package com.restaurant.catalogapi.controller;

import com.restaurant.catalogapi.dto.AuthResponse;
import com.restaurant.catalogapi.dto.LoginRequest;
import com.restaurant.catalogapi.dto.MessageResponse;
import com.restaurant.catalogapi.dto.PasswordResetRequest;
import com.restaurant.catalogapi.dto.RegisterRequest;
import com.restaurant.catalogapi.dto.ResetPasswordRequest;
import com.restaurant.catalogapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Registers a new user and returns a JWT token.
     */
    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    /**
     * POST /api/auth/login
     * Authenticates a user and returns a JWT token.
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request)
                .map(ResponseEntity::ok);
    }

    /**
     * POST /api/auth/forgot-password
     * Sends a password reset link to the provided email.
     */
    @PostMapping("/forgot-password")
    public Mono<ResponseEntity<MessageResponse>> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        return authService.requestPasswordReset(request)
                .map(ResponseEntity::ok);
    }

    /**
     * POST /api/auth/reset-password
     * Resets the user's password using a valid reset token.
     */
    @PostMapping("/reset-password")
    public Mono<ResponseEntity<MessageResponse>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request)
                .map(ResponseEntity::ok);
    }
}
