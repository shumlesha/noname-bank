package com.bank.userservice.mapper;

import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "banned", ignore = true)
    User toEntity(UserCreatePayload payload);

    UserDto toDto(User save);
}
