package ru.patterns.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.patterns.gateway.model.DefaultResponse;
import ru.patterns.gateway.model.TokenVerificationDto;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @PostMapping("/api/auth/verify")
    ResponseEntity<DefaultResponse<TokenVerificationDto>> validateToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    );
}
