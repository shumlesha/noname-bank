package com.bank.authservice.service.impl;

import com.bank.authservice.mapper.AuthMapper;
import com.bank.authservice.dto.user.RegisterUserRequest;
import com.bank.authservice.dto.user.SaveUserRequest;
import com.bank.authservice.dto.user.UserDto;
import com.bank.authservice.entity.UserCredentials;
import com.bank.authservice.repository.UserCredentialsRepository;
import com.bank.authservice.service.AuthService;
import com.bank.authservice.service.UserServiceClient;
import com.bank.authservice.validator.UserCredentialsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final UserCredentialsValidator userCredentialsValidator;
    private final UserServiceClient userServiceClient;
    private final AuthMapper authMapper;

    @Override
    @Transactional
    public void registerUser(RegisterUserRequest registerUserRequest) {
        userCredentialsValidator.checkUserAlreadyExists(registerUserRequest.getEmail());

        SaveUserRequest request = authMapper.toSaveUserRequest(registerUserRequest);
        UserDto savedUser = userServiceClient.saveUser(request);

        UserCredentials userCredentials = authMapper.toUserCredentials(savedUser.getId(), registerUserRequest);

        userCredentialsRepository.save(userCredentials);
    }
}
