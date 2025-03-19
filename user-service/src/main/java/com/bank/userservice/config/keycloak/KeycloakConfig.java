package com.bank.userservice.config.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {
    private final KeycloakProperties keycloakProperties;

    @Bean
    public Keycloak keycloakAdminClient() {
        KeycloakProperties.Admin adminProperties = keycloakProperties.getAdmin();

        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .realm(keycloakProperties.getRealm())
                .username(adminProperties.getUsername())
                .password(adminProperties.getPassword())
                .clientId(adminProperties.getClientId())
                .clientSecret(adminProperties.getClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    @Bean(name = "keycloakRealmResource")
    public RealmResource realmResource(Keycloak keycloakAdminClient) {
        return keycloakAdminClient.realm(keycloakProperties.getRealm());
    }

    @Bean(name = "keycloakUsersResource")
    public UsersResource usersResource(RealmResource keycloakRealmResource) {
        return keycloakRealmResource.users();
    }

    @Bean(name = "keycloakRolesResource")
    public RolesResource rolesResource(RealmResource keycloakRealmResource) {
        return keycloakRealmResource.roles();
    }
}
