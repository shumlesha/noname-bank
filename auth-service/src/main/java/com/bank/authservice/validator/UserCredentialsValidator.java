package com.bank.authservice.validator;

import com.bank.authservice.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCredentialsValidator {
    private final UserCredentialsRepository userCredentialsRepository;

    public void checkUserAlreadyExists(String email) {
        if (userCredentialsRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }
    }
}
