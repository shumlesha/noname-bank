package ru.patterns.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {
    public Mono<String> getLoginUrl() {
        return Mono.just("/oauth2/authorization/keycloak");
    }
}
