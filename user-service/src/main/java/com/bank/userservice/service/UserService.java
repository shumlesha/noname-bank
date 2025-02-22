package com.bank.userservice.service;

import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.entity.User;
import java.util.UUID;

public interface UserService {
    void createUser(UserCreatePayload payload);

    UserDto banUser(UUID userId, UUID currentUserId);

    User getByEmail(String email);
}
