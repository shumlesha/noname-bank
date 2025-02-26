package com.bank.authservice.security.service;

import java.util.UUID;

public interface TokenStorageService {
    void storeRefreshToken(String tokenId, UUID userId);

    boolean isAccessTokenRevoked(String tokenId);

    boolean isRefreshTokenPresentForUser(String tokenId, UUID userId);

    void revokeAccessToken(String tokenId);

    void removeRefreshToken(String tokenId);
}
