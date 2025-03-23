package ru.patterns.gateway.configuration.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfiguration {
    private final TokenRelayGatewayFilterFactory customTokenRelayFactory;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f
                                .filter(customTokenRelayFactory.apply())
                                .addRequestHeader("X-Service", "gateway"))
                        .uri("lb://user-service"))
                .route("core-query", r -> r.path("/api/query/account/**", "/api/query/transaction/**")
                        .filters(f -> f
                                .filter(customTokenRelayFactory.apply())
                                .addRequestHeader("X-Service", "gateway"))
                        .uri("lb://core-query"))
                .route("core", r -> r.path("/api/account/**", "/api/transaction/**")
                        .filters(f -> f
                                .filter(customTokenRelayFactory.apply())
                                .addRequestHeader("X-Service", "gateway"))
                        .uri("lb://core"))
                .route("atm", r -> r.path("/api/atm/**")
                        .filters(f -> f
                                .filter(customTokenRelayFactory.apply())
                                .addRequestHeader("X-Service", "gateway"))
                        .uri("lb://atm"))
                .route("client-root-redirect", r -> r.path("/client", "/client/")
                        .filters(f -> f.redirect(302, "/client/login"))
                        .uri("lb://client-interface"))
                .route("client-interface", r -> r.path("/client/**")
                        .filters(f -> f.filter(customTokenRelayFactory.apply()))
                        .uri("lb://client-interface"))
                .route("employee-root-redirect", r -> r.path("/employee", "/employee/")
                        .filters(f -> f.redirect(302, "/employee/login"))
                        .uri("lb://employee-interface"))
                .route("employee-interface", r -> r.path("/employee/**")
                        .filters(f -> f.filter(customTokenRelayFactory.apply()))
                        .uri("lb://employee-interface"))
//                .route("root-redirect", r -> r.path("/")
//                        .filters(f -> f.redirect(302, "/client/login"))
//                        .uri("lb://client-interface"))
                .build();
    }
}
