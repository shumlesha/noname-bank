package ru.patterns.gateway.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private Collection<String> extractRoles(OidcIdToken oidcIdToken) {
        Collection<String> roles = oidcIdToken.getClaimAsStringList("roles");
        return roles != null ? roles : Collections.emptySet();
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
