package ru.patterns.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @GetMapping("/auth/validate")
    ResponseEntity<String> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
