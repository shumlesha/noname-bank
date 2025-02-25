package com.bank.authservice.mapper;

import com.bank.authservice.dto.user.RegisterUserRequest;
import com.bank.authservice.dto.user.SaveUserRequest;
import com.bank.authservice.entity.UserCredentials;
import com.bank.authservice.security.PasswordEncoderMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = PasswordEncoderMapper.class)
public interface AuthMapper {
    SaveUserRequest toSaveUserRequest(RegisterUserRequest registerUserRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", qualifiedByName = "passwordHash")
    UserCredentials toUserCredentials(UUID userId, RegisterUserRequest registerUserRequest);

}
