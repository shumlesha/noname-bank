package ru.patterns.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.patterns.gateway.client.AuthServiceClient;
import ru.patterns.gateway.model.DefaultResponse;
import ru.patterns.gateway.model.TokenVerificationDto;

@Component
@Slf4j
public class AuthFilter implements GatewayFilter {

    private final AuthServiceClient authServiceClient;

    public AuthFilter(@Lazy AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        var token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.length() > 7) {
            token = token.substring(7);
        }
        log.warn("Authorization header is {}", token);

        if (token == null || token.isEmpty()) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        String finalToken = token;
        return Mono.fromCallable(() -> authServiceClient.validateToken(finalToken))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(response -> {
                    log.warn(response.getBody().getData().toString());
                    if (response.getStatusCode() != HttpStatus.OK ||
                            response.getBody() == null ||
                            response.getBody().getData() == null ||
                            !response.getBody().getData().isVerified()) {
                        return onError(exchange, HttpStatus.UNAUTHORIZED);
                    }

                    var userId = response.getBody().getData().getUserId().toString();

                    var modifiedRequest = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                })
                .onErrorResume(e -> {
                    log.error("Ошибка: ", e);
                    return onError(exchange, HttpStatus.SERVICE_UNAVAILABLE);
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}

