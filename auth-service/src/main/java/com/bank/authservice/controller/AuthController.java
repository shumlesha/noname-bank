package com.bank.authservice.controller;

import com.bank.authservice.dto.api.DefaultResponse;
import com.bank.authservice.dto.auth.LoginUserRequest;
import com.bank.authservice.dto.auth.LogoutRequest;
import com.bank.authservice.dto.auth.RefreshTokenRequest;
import com.bank.authservice.dto.auth.RegisterResponse;
import com.bank.authservice.dto.auth.RegisterUserRequest;
import com.bank.authservice.dto.auth.TokenDto;
import com.bank.authservice.dto.auth.TokenVerificationDto;
import com.bank.authservice.service.AuthService;
import com.bank.authservice.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<DefaultResponse<RegisterResponse>> registerUser(
            @RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(
                ResponseBuilder.success(
                        "User registered successfully",
                        authService.registerUser(registerUserRequest)
                )
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public ResponseEntity<DefaultResponse<TokenDto>> loginUser(@RequestBody LoginUserRequest loginUserRequest) {
        return ResponseEntity.ok(
                ResponseBuilder.success(
                        "User logged in successfully",
                        authService.loginUser(loginUserRequest)
                )
        );
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token")
    public ResponseEntity<DefaultResponse<TokenDto>> refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(
                ResponseBuilder.success(
                        "Token refreshed successfully",
                        authService.refreshToken(refreshTokenRequest)
                )
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout a user")
    public ResponseEntity<DefaultResponse<String>> logoutUser(@RequestBody LogoutRequest logoutRequest,
                                                              @Header("Authorization") String authHeader) {
        authService.logoutUser(logoutRequest, authHeader);
        return ResponseEntity.ok(
                ResponseBuilder.success(
                        "User logged out successfully"
                )
        );
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify access token")
    public ResponseEntity<DefaultResponse<TokenVerificationDto>> verifyAccessToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        TokenVerificationDto result = authService.verifyAccessToken(authHeader);
        return ResponseEntity.ok(
                ResponseBuilder.success(
                        "Access token verified successfully",
                        result
                )
        );
    }

}
