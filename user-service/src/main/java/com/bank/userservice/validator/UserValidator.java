package com.bank.userservice.validator;

import com.bank.userservice.entity.User;
import com.bank.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void checkUserAlreadyExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }
    }

    public User validateBan(UUID userId, UUID currentUserId) {
        if (userId.equals(currentUserId)) {
            throw new IllegalArgumentException("You cannot ban yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isBanned()) {
            throw new IllegalArgumentException("User is already banned");
        }

        return user;
    }
}
