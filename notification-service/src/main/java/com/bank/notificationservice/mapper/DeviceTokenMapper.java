package com.bank.notificationservice.mapper;

import com.bank.notificationservice.dto.token.RegisterTokenRequest;
import com.bank.notificationservice.dto.token.TokenDto;
import com.bank.notificationservice.entity.DeviceToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DeviceTokenMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "userId", conditionExpression = "java(shouldUpdateUserId(deviceToken, userId))", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDeviceTokenFromRequest(@MappingTarget DeviceToken deviceToken, RegisterTokenRequest registerTokenRequest,
                                      UUID userId);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userId", source = "userId")
    DeviceToken toDeviceToken(RegisterTokenRequest registerTokenRequest, UUID userId);

    TokenDto toTokenDto(DeviceToken deviceToken);

    default boolean shouldUpdateUserId(DeviceToken deviceToken, UUID userId) {
        return deviceToken.getUserId() == null || !deviceToken.getUserId().equals(userId);
    }
}
