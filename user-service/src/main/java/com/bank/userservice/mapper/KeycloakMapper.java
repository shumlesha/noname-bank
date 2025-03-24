package com.bank.userservice.mapper;

import com.bank.userservice.dto.user.RoleDto;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.enumeration.Gender;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface KeycloakMapper {

    @Mapping(target = "roles", expression = "java(getRoles(userRepresentation))")
    @Mapping(target = "gender", expression = "java(getGender(attributes))")
    @Mapping(target = "fullName", expression = "java(getName(attributes))")
    @Mapping(target = "banned", expression = "java(!userRepresentation.isEnabled())")
    @Mapping(target = "id", source = "userRepresentation.id")
    @Mapping(target = "email", source = "userRepresentation.email")
    UserDto toDto(UserRepresentation userRepresentation, Map<String, List<String>> attributes);

    @Named("getName")
    default String getName(Map<String, List<String>> attributes) {
        return attributes.get("fullName").getFirst();
    }

    @Named("getGender")
    default Gender getGender(Map<String, List<String>> attributes) {
        return Gender.valueOf(attributes.get("gender").getFirst());
    }

    @Named("getRoles")
    default Set<RoleDto> getRoles(UserRepresentation userRepresentation) {
        return userRepresentation.getAttributes().get("roles").stream()
                .map(RoleDto::new)
                .collect(Collectors.toSet());
    }
}
