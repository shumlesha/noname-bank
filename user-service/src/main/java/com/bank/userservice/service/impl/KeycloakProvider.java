package com.bank.userservice.service.impl;

import com.bank.userservice.dto.user.BanUserRequest;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.exception.BadRequestException;
import com.bank.userservice.exception.EntityNotFoundException;
import com.bank.userservice.mapper.KeycloakMapper;
import com.bank.userservice.service.IdentityProvider;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeycloakProvider implements IdentityProvider {
    private final UsersResource keycloakUserResource;
    private final KeycloakMapper keycloakMapper;

    @Override
    public UserDto banUser(UUID userId, BanUserRequest banUserRequest) {
        UserResource userResource = keycloakUserResource.get(userId.toString());
        UserRepresentation user = userResource.toRepresentation();

        if (Boolean.FALSE.equals(user.isEnabled())) {
            throw new BadRequestException("User is already banned");
        }

        user.setEnabled(false);
        userResource.update(user);

        UserRepresentation updatedUser = keycloakUserResource.get(userId.toString()).toRepresentation();

        return keycloakMapper.toDto(
                updatedUser,
                updatedUser.getAttributes()
        );
    }

    @Override
    public UserDto getByEmail(String email) {
        UserRepresentation userRepresentation = keycloakUserResource.search(email).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User", email));

        return keycloakMapper.toDto(
                userRepresentation,
                userRepresentation.getAttributes()
        );
    }

    @Override
    public UserDto getById(UUID userId) {
        UserRepresentation userRepresentation = keycloakUserResource.get(userId.toString()).toRepresentation();

        return keycloakMapper.toDto(
                userRepresentation,
                userRepresentation.getAttributes()
        );
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();

        List<UserRepresentation> usersResource = keycloakUserResource.list(offset, pageSize);

        List<UserDto> users = usersResource.stream().map(userRepresentation -> keycloakMapper.toDto(
                userRepresentation,
                userRepresentation.getAttributes()
        )).toList();

        int total = users.size();

        return new PageImpl<>(users, pageable, total);
    }
}
