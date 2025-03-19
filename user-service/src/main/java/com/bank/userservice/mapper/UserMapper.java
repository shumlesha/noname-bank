package com.bank.userservice.mapper;

import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.dto.user.RoleDto;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.entity.User;
import com.bank.userservice.security.CurrentUser;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "banned", ignore = true)
    User toEntity(UserCreatePayload payload);

    UserDto toDto(User save);

    UserDto toSelfEntity(CurrentUser currentUser);

    @Mapping(target = "name", source = "roleName")
    RoleDto toRoleDto(String roleName);
}
