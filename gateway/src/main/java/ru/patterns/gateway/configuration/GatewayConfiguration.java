package ru.patterns.gateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.patterns.gateway.filter.AuthFilter;

@Configuration
public class GatewayConfiguration {
    private final AuthFilter authFilter;

    public GatewayConfiguration(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                .route("credit-service", r -> r.path("/api/credit/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://credit-service"))
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://user-service"))
                .route("core-query", r -> r.path("/api/query/account/**", "/api/query/transaction/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://core-query"))
                .route("core", r -> r.path("/api/account/**", "/api/transaction/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://core"))
                .route("atm", r -> r.path("/api/atm/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://atm"))
                .build();
    }
}
