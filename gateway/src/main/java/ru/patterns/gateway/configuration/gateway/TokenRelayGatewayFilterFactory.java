package ru.patterns.gateway.configuration.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;

@Component("customTokenRelayFactory")
@Slf4j
public class TokenRelayGatewayFilterFactory
        extends AbstractGatewayFilterFactory<TokenRelayGatewayFilterFactory.Config> {
    private static final int TOKEN_EXPIRATION_THRESHOLD = 60;

    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;
    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    public TokenRelayGatewayFilterFactory(ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
                                          ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        super(Config.class);
        this.authorizedClientRepository = authorizedClientRepository;
        this.authorizedClientManager = authorizedClientManager;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> exchange.getPrincipal()
                .filter(Authentication.class::isInstance)
                .cast(Authentication.class)
                .filter(OAuth2AuthenticationToken.class::isInstance)
                .cast(OAuth2AuthenticationToken.class)
                .flatMap(oAuth2Authentication -> {
                    String clientRegistrationId = oAuth2Authentication.getAuthorizedClientRegistrationId();
                    return authorizedClientRepository.loadAuthorizedClient(
                                    clientRegistrationId,
                                    oAuth2Authentication,
                                    exchange)
                            .cast(OAuth2AuthorizedClient.class)
                            .flatMap(authorizedClient -> {
                                if (isExpired(authorizedClient.getAccessToken())) {
                                    log.info("Access token has expired. Attempting to refresh token for client {}",
                                            clientRegistrationId);
                                    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                                            .withClientRegistrationId(clientRegistrationId)
                                            .principal(oAuth2Authentication)
                                            .attribute(ServerWebExchange.class.getName(), exchange)
                                            .build();
                                    return authorizedClientManager.authorize(authorizeRequest)
                                            .flatMap(refreshedClient -> {
                                                if (refreshedClient != null &&
                                                        refreshedClient.getAccessToken() != null) {
                                                    log.info("Token refreshed successfully for client {}",
                                                            clientRegistrationId);
                                                    return relayToken(refreshedClient, exchange, chain);
                                                } else {
                                                    log.error("Failed to refresh token for client {}",
                                                            clientRegistrationId);
                                                    return handleTokenRefreshFailure(exchange);
                                                }
                                            })
                                            .onErrorResume(e -> {
                                                log.error("Error refreshing token", e);
                                                return handleTokenRefreshFailure(exchange);
                                            });
                                } else {
                                    return relayToken(authorizedClient, exchange, chain);
                                }
                            });
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> relayToken(OAuth2AuthorizedClient authorizedClient, ServerWebExchange exchange,
                                  org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.header("Authorization", "Bearer " + accessToken.getTokenValue()))
                .build();
        return chain.filter(mutatedExchange);
    }

    private Mono<Void> handleTokenRefreshFailure(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private boolean isExpired(OAuth2AccessToken accessToken) {
        Instant now = Instant.now();
        Instant expiresAt = accessToken.getExpiresAt();
        return expiresAt != null && now.isAfter(expiresAt.minus(Duration.ofSeconds(TOKEN_EXPIRATION_THRESHOLD)));
    }

    public GatewayFilter apply() {
        return apply(new Config());
    }
}
