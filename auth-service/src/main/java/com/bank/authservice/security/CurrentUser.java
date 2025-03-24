package com.bank.authservice.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import java.security.Principal;
import java.util.Collection;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


}
