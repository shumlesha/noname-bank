package ru.patterns.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.patterns.gateway.configuration.keycloak.admin.KeycloakProperties;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final KeycloakProperties keycloakProperties;
    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    public Mono<URI> logout(ServerWebExchange exchange, Authentication authentication) {
        return clearLocalSession(exchange, authentication)
                .then(buildKeycloakLogoutUrl(exchange));
    }

    private Mono<Void> clearLocalSession(ServerWebExchange exchange, Authentication authentication) {
        if (authentication == null) {
            return Mono.empty();
        }

        Mono<Void> removeAuthorizedClientMono = Mono.empty();
        if (authentication.getPrincipal() instanceof OidcUser) {
            removeAuthorizedClientMono = clientRegistrationRepository.findByRegistrationId("keycloak")
                    .flatMap(clientRegistration ->
                        authorizedClientRepository.removeAuthorizedClient(
                                clientRegistration.getRegistrationId(),
                                authentication,
                                exchange
                        )
                    );
        }

        return removeAuthorizedClientMono.then(exchange.getSession().flatMap(WebSession::invalidate));
    }

    private Mono<URI> buildKeycloakLogoutUrl(ServerWebExchange exchange) {
        String baseUrl = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                .replacePath("/")
                .replaceQuery(null)
                .build()
                .toUriString();

        String logoutUrl = UriComponentsBuilder
                .fromUriString(keycloakProperties.getIssuerUri())
                .path("/protocol/openid-connect/logout")
                .queryParam("post_logout_redirect_uri", baseUrl)
                .queryParam("client_id", keycloakProperties.getClientId())
                .toUriString();

        return Mono.just(URI.create(logoutUrl));
    }
}
