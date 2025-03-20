package ru.patterns.gateway.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String issuerUri;
    private String jwkSetUri;
    private String clientId;
    private String clientSecret;
    private String authorizationGrantType;
    private String redirectUri;
    private String scope;
}
