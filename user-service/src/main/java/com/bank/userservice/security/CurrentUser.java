package com.bank.userservice.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class CurrentUser implements UserDetails {
    private final UUID id;
    private final String email;
    private final String fullName;
    private final Collection<? extends GrantedAuthority> authorities;


    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }
}
