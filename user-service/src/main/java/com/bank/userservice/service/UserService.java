package com.bank.userservice.service;

import com.bank.userservice.dto.event.payload.UserCreatePayload;

public interface UserService {
    void createUser(UserCreatePayload payload);
}
