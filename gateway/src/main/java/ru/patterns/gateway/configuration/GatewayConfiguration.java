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
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://user-service"))
                .build();
    }
}
