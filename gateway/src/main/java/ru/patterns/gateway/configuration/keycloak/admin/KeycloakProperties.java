package ru.patterns.gateway.configuration.keycloak.admin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String authServerUrl;
    private String issuerUri;
    private String jwkSetUri;
    private String clientId;
    private String clientSecret;
    private String authorizationGrantType;
    private String logoutRedirectUri;
    private String redirectUri;
    private String scope;
    private String realm;

    private Admin admin = new Admin();

    @Data
    public static class Admin {
        private String realm;
        private String username;
        private String password;
        private String clientSecret;
        private String clientId;
    }
}
