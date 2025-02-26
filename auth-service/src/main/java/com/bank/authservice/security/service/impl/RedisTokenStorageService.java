package com.bank.authservice.security.service.impl;

import com.bank.authservice.security.JwtProperties;
import com.bank.authservice.security.service.TokenStorageService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RMapCache;
import org.redisson.api.RSetCache;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisTokenStorageService implements TokenStorageService {
    private final RBloomFilter<String> revokedTokensBloomFilter;
    private final RMapCache<String, String> refreshTokensMap;
    private final RSetCache<String> revokedTokensSet;
    private final JwtProperties jwtProperties;

    @Override
    public void storeRefreshToken(String tokenId, UUID userId) {
        refreshTokensMap.put(tokenId, userId.toString(), jwtProperties.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isAccessTokenRevoked(String tokenId) {
        if (revokedTokensBloomFilter.contains(tokenId)) {
            return revokedTokensSet.contains(tokenId);
        }
        return false;
    }

    @Override
    public boolean isRefreshTokenPresentForUser(String tokenId, UUID userId) {
        String storedUserId = refreshTokensMap.get(tokenId);
        return storedUserId != null && storedUserId.equals(userId.toString());
    }

    @Override
    public void revokeAccessToken(String tokenId) {
        revokedTokensBloomFilter.add(tokenId);
        revokedTokensSet.add(tokenId, jwtProperties.getAccessTokenExpiration(),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void removeRefreshToken(String tokenId) {
        refreshTokensMap.remove(tokenId);
    }
}
