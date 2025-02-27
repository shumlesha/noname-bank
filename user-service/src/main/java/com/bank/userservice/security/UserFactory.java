package com.bank.userservice.security;

import com.bank.userservice.dto.user.RoleDto;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.entity.Role;
import com.bank.userservice.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.Set;

@UtilityClass
public class UserFactory {
    private static final String ROLE_PREFIX = "ROLE_";

    public CurrentUser create(UserDto user) {
        return new CurrentUser(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                mapToGrantedAuthorities(user.getRoles())
        );
    }

    private static List<SimpleGrantedAuthority> mapToGrantedAuthorities(Set<RoleDto> roles) {
        return roles.stream()
                .map(RoleDto::getName)
                .map(ROLE_PREFIX::concat)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
