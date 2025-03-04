package com.bank.userservice.service;

import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.dto.user.BanUserRequest;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface UserService {
    UserDto createUser(UserCreatePayload payload);

    UserDto banUser(UUID userId, BanUserRequest banUserRequest, UUID currentUserId);

    UserDto getByEmail(String email);

    UserDto getUserById(UUID id);

    Page<UserDto> getAllUsers(Pageable pageable);
}
