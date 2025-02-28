package com.bank.userservice.validator;

import com.bank.userservice.entity.User;
import com.bank.userservice.exception.BadRequestException;
import com.bank.userservice.exception.EntityAlreadyExistsException;
import com.bank.userservice.exception.EntityNotFoundException;
import com.bank.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void checkUserAlreadyExists(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EntityAlreadyExistsException("User", "email", email);
        }
    }

    public User validateBan(UUID userId, UUID currentUserId) {
        if (userId.equals(currentUserId)) {
            throw new BadRequestException("You cannot ban yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        if (user.isBanned()) {
            throw new BadRequestException("User is already banned");
        }

        return user;
    }
}
