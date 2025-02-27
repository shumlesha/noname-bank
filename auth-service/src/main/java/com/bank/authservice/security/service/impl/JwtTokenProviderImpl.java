package com.bank.authservice.security.service.impl;

import com.bank.authservice.enumeration.TokenType;
import com.bank.authservice.security.JwtProperties;
import com.bank.authservice.security.service.JwtTokenProvider;
import com.bank.authservice.security.service.TokenStorageService;
import com.bank.authservice.util.TokenUtil;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private final TokenStorageService redisTokenService;

    @Override
    public String createAccessToken(UUID userId, String email, List<String> roles) {
        return createToken(userId, email, roles, jwtProperties.getAccessTokenSecret(),
                jwtProperties.getAccessTokenExpiration());
    }

    @Override
    public String createRefreshToken(UUID userId, String email) {
        String refreshToken = createToken(userId, email, null, jwtProperties.getRefreshTokenSecret(),
                jwtProperties.getRefreshTokenExpiration());
        redisTokenService.storeRefreshToken(extractTokenId(refreshToken), userId);
        return refreshToken;
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            boolean isCorrectSignature = checkSignature(signedJWT, TokenType.ACCESS);

            if (!isCorrectSignature) {
                return false;
            }

            if (redisTokenService.isAccessTokenRevoked(signedJWT.getJWTClaimsSet().getJWTID())) {
                return false;
            }

            return checkExpiration(signedJWT);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void revokeAccessToken(String token) {
        redisTokenService.revokeAccessToken(extractTokenId(token));
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            boolean isCorrectSignature = checkSignature(signedJWT, TokenType.REFRESH);

            if (!isCorrectSignature) {
                return false;
            }

            String tokenId = extractTokenId(token);
            UUID userId = extractUserId(token);

            if (!redisTokenService.isRefreshTokenPresentForUser(tokenId, userId)) {
                return false;
            } else {
                redisTokenService.removeRefreshToken(tokenId);
            }

            return checkExpiration(signedJWT);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractEmail(String token) {
        try {
            JWTClaimsSet claims = SignedJWT.parse(token).getJWTClaimsSet();
            return claims.getSubject();
        } catch (ParseException e) {
            return null;
        }
    }


    private boolean checkSignature(SignedJWT signedJWT, TokenType tokenType) {
        try {
            String secret;
            if (tokenType == TokenType.ACCESS) {
                secret = jwtProperties.getAccessTokenSecret();
            } else {
                secret = jwtProperties.getRefreshTokenSecret();
            }
            JWSVerifier verifier = new MACVerifier(secret);

            return signedJWT.verify(verifier);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkExpiration(SignedJWT signedJWT) {
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationDate = claims.getExpirationTime();
            return expirationDate != null && expirationDate.after(new Date());
        } catch (ParseException e) {
            return false;
        }

    }

    private String extractTokenId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            return null;
        }
    }

    private UUID extractUserId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return UUID.fromString(signedJWT.getJWTClaimsSet().getClaim("userId").toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String createToken(UUID userId, String email, List<String> roles, String secret, long expiration) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .jwtID(TokenUtil.generateJti())
                .subject(email)
                .issuer("auth-service")
                .issueTime(now)
                .expirationTime(expirationDate)
                .claim("roles", roles)
                .claim("userId", userId)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);

        try {
            JWSSigner signer = new MACSigner(secret);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create token");
        }
    }
}
