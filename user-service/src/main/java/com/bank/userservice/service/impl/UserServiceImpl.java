package com.bank.userservice.service.impl;

import com.bank.userservice.aspect.PublishBanEvent;
import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.dto.user.BanUserRequest;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.entity.Role;
import com.bank.userservice.entity.User;
import com.bank.userservice.mapper.UserMapper;
import com.bank.userservice.repository.RoleRepository;
import com.bank.userservice.repository.UserRepository;
import com.bank.userservice.service.UserService;
import com.bank.userservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserCreatePayload payload) {
        userValidator.checkUserAlreadyExists(payload.getEmail());
        List<Role> roles = roleRepository.findAllByNameIn(payload.getRoles());

        User user = userMapper.toEntity(payload);
        user.setRoles(Set.copyOf(roles));

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @PublishBanEvent(topics = {"user_blocked"})
    @Transactional
    public UserDto banUser(UUID userId, BanUserRequest banUserRequest, UUID currentUserId) {
        User user = userValidator.validateBan(userId, currentUserId);

        user.ban();

        return userMapper.toDto(userRepository.save(user));
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto getByEmail(String email) {
        User user = userRepository.findByEmailReadOnly(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }
}
