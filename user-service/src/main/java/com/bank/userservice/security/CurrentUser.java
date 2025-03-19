package com.bank.userservice.security;

import com.bank.userservice.enumeration.Gender;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class CurrentUser implements Principal, OAuth2AuthenticatedPrincipal {
    private final UUID id;
    private final String email;
    private final Map<String, Object> attributes;
    private final Collection<GrantedAuthority> authorities;

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getFullName() {
        return attributes.get("fullName").toString();
    }

    public Gender getGender() {
        return Gender.valueOf(attributes.get("gender").toString());
    }

    public boolean isBanned() {
        return !((boolean) attributes.get("user_enabled"));
    }

    public List<String> getRoles() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.substring(5))
                .toList();
    }
}
