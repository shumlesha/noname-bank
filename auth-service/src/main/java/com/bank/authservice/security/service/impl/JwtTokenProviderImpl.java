package com.bank.authservice.security.service.impl;

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
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private final TokenStorageService redisTokenService;

    @Override
    public String createAccessToken(String email, List<String> roles) {
        return createToken(email, roles, jwtProperties.getAccessTokenSecret(),
                jwtProperties.getAccessTokenExpiration());
    }

    @Override
    public String createRefreshToken(String email) {
        String refreshToken = createToken(email, null, jwtProperties.getRefreshTokenSecret(),
                jwtProperties.getRefreshTokenExpiration());
        redisTokenService.storeRefreshToken(extractTokenId(refreshToken), email);
        return refreshToken;
    }

    @Override
    public boolean validateToken(String token, boolean isRefreshToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(
                    isRefreshToken ? jwtProperties.getRefreshTokenSecret() : jwtProperties.getAccessTokenSecret());

            if (!signedJWT.verify(verifier)) {
                return false;
            }

            if (!isRefreshToken && redisTokenService.isAccessTokenRevoked(signedJWT.getJWTClaimsSet().getJWTID())) {
                return false;
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationDate = claims.getExpirationTime();

            return expirationDate != null && expirationDate.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private String extractTokenId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    private String createToken(String email, List<String> roles, String secret, long expiration) {
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
