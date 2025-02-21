package com.bank.userservice.service.impl;

import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.entity.User;
import com.bank.userservice.mapper.UserMapper;
import com.bank.userservice.repository.UserRepository;
import com.bank.userservice.service.UserService;
import com.bank.userservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void createUser(UserCreatePayload payload) {
        userValidator.checkUserAlreadyExists(payload.getEmail());

        User user = userMapper.toEntity(payload);

        userRepository.save(user);
    }
}
