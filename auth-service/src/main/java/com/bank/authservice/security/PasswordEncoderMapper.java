package com.bank.authservice.security;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderMapper {
    private final PasswordEncoder passwordEncoder;

    @Named("passwordHash")
    public String passwordHash(String password) {
        return password != null ? passwordEncoder.encode(password) : null;
    }
}
