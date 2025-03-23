package ru.patterns.gateway.configuration.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("customTokenRelayFactory")
@Slf4j
public class TokenRelayGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenRelayGatewayFilterFactory.Config> {

    public TokenRelayGatewayFilterFactory() {
        super(Config.class);
        log.info("Custom TokenRelayGatewayFilterFactory initialized");
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .map(SecurityContext::getAuthentication)
                .filter(a -> a.getPrincipal() instanceof Jwt)
                .map(Authentication::getPrincipal)
                .cast(Jwt.class)
                .map(jwt -> {
                    String token = jwt.getTokenValue();
                    log.debug("Relaying token to downstream service");
                    return exchange.mutate()
                            .request(r -> r.header("Authorization", "Bearer " + token))
                            .build();
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    public GatewayFilter apply() {
        return apply(new Config());
    }
}
