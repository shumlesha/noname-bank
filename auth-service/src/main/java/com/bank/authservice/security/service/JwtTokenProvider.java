package com.bank.authservice.security.service;

import java.util.List;

public interface JwtTokenProvider {
    String createAccessToken(String email, List<String> roles);

    String createRefreshToken(String email);

    boolean validateToken(String token, boolean isRefreshToken);
}
