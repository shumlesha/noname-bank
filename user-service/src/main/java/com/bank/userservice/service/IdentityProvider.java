package com.bank.userservice.service;

import com.bank.userservice.dto.user.BanUserRequest;
import com.bank.userservice.dto.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IdentityProvider {
    UserDto banUser(UUID userId, BanUserRequest banUserRequest);
    UserDto getByEmail(String email);
    UserDto getById(UUID userId);
    Page<UserDto> getAllUsers(Pageable pageable);
}
