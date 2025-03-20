package ru.patterns.gateway.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfiguration {

    private final KeycloakProperties keycloakProperties;

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryReactiveClientRegistrationRepository(keycloakClientRegistration());
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    private ClientRegistration keycloakClientRegistration() {
        return ClientRegistrations
                .fromIssuerLocation(keycloakProperties.getIssuerUri())
                .registrationId("keycloak")
                .clientId(keycloakProperties.getClientId())
                .clientSecret(keycloakProperties.getClientSecret())
                .authorizationGrantType(
                        new AuthorizationGrantType(keycloakProperties.getAuthorizationGrantType()))
                .redirectUri(keycloakProperties.getRedirectUri())
                .scope(keycloakProperties.getScope().split(","))
                .build();
    }
}
