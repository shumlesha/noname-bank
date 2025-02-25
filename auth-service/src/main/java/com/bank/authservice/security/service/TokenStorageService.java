package com.bank.authservice.security.service;

import java.util.UUID;

public interface TokenStorageService {
    void storeRefreshToken(String tokenId, String email);
    boolean isAccessTokenRevoked(String tokenId);
    void revokeAccessToken(String tokenId);
}
