package ru.patterns.gateway.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    public Mono<UserInfo> getUserInfo(OidcIdToken oidcIdToken) {
        UserInfo userInfo = new UserInfo();

        userInfo.setId(oidcIdToken.getSubject());
        userInfo.setEmail(oidcIdToken.getClaimAsString("email"));
        userInfo.setFullName(oidcIdToken.getClaimAsString("fullName"));

        Set<String> roles = new HashSet<>(extractRoles(oidcIdToken));

        userInfo.setRoles(roles);

        return Mono.just(userInfo);
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractRoles(OidcIdToken oidcIdToken) {
        Set<String> allRoles = new HashSet<>();

        extractRolesFromClaim(oidcIdToken, allRoles);

        String clientId = Optional.ofNullable(oidcIdToken.getClaimAsString("azp"))
                .orElse("app-client");

        extractClientRoles(oidcIdToken, clientId, allRoles);

        return allRoles.stream().filter(role -> role.equals(role.toUpperCase())).collect(Collectors.toSet());
    }


    @SuppressWarnings("unchecked")
    private void extractRolesFromClaim(OidcIdToken token, Set<String> targetSet) {
        Optional.ofNullable(token.getClaimAsMap("realm_access"))
                .map(claim -> (Collection<String>) claim.get("roles"))
                .ifPresent(targetSet::addAll);
    }


    @SuppressWarnings("unchecked")
    private void extractClientRoles(OidcIdToken token, String clientId, Set<String> targetSet) {
        Optional.ofNullable(token.getClaimAsMap("resource_access"))
                .map(resourceAccess -> resourceAccess.get(clientId))
                .map(clientAccess -> (Map<String, Object>) clientAccess)
                .map(clientAccess -> (Collection<String>) clientAccess.get("roles"))
                .ifPresent(targetSet::addAll);
    }

    @Data
    public static class UserInfo {
        private String id;
        private String email;
        private String fullName;
        private Set<String> roles = new HashSet<>();

        public boolean hasRole(String role) {
            return roles.stream()
                    .anyMatch(r -> r.equalsIgnoreCase(role));
        }
    }
}
