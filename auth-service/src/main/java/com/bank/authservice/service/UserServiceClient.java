package com.bank.authservice.service;

import com.bank.authservice.dto.user.GetUserRequest;
import com.bank.authservice.dto.user.SaveUserRequest;
import com.bank.authservice.dto.user.UserDto;

public interface UserServiceClient {
    UserDto saveUser(SaveUserRequest saveUserRequest);

    UserDto getUserByEmail(GetUserRequest getUserRequest);
}
