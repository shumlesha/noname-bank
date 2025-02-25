package com.bank.authservice.service;

import com.bank.authservice.dto.user.RegisterUserRequest;

public interface AuthService {
    void registerUser(RegisterUserRequest registerUserRequest);
}
