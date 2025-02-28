package com.bank.authservice.security.service;

import java.util.List;
import java.util.UUID;

public interface JwtTokenProvider {
    String createAccessToken(UUID userId, String email, List<String> roles);

    String createRefreshToken(UUID userId, String email);

    boolean validateAccessToken(String token);

    void revokeAccessToken(String token);

    boolean validateRefreshToken(String token);

    String extractEmail(String token);

    UUID extractUserId(String token);
}
