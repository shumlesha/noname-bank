package com.bank.authservice.service;

import com.bank.authservice.dto.auth.LoginUserRequest;
import com.bank.authservice.dto.auth.LogoutRequest;
import com.bank.authservice.dto.auth.RefreshTokenRequest;
import com.bank.authservice.dto.auth.RegisterResponse;
import com.bank.authservice.dto.auth.RegisterUserRequest;
import com.bank.authservice.dto.auth.TokenDto;
import com.bank.authservice.dto.auth.TokenVerificationDto;

public interface AuthService {
    RegisterResponse registerUser(RegisterUserRequest registerUserRequest);

    TokenDto loginUser(LoginUserRequest loginUserRequest);

    TokenDto refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logoutUser(LogoutRequest logoutRequest, String authHeader);

    TokenVerificationDto verifyAccessToken(String authHeader);
}
