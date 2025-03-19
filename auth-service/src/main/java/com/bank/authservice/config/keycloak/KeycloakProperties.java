package com.bank.authservice.config.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String authServerUrl;
    private String realm;
    private String resource;
    private Boolean bearerOnly;

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
