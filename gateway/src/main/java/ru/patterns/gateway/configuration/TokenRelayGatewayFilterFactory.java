package ru.patterns.gateway.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenRelayGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    public TokenRelayGatewayFilterFactory() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .map(SecurityContext::getAuthentication)
                .filter(a -> a.getPrincipal() instanceof Jwt)
                .map(Authentication::getPrincipal)
                .cast(Jwt.class)
                .map(jwt -> {
                    String token = jwt.getTokenValue();

                    exchange.getRequest().mutate()
                            .header("Authorization", "Bearer " + token)
                            .build();

                    return exchange;
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}
