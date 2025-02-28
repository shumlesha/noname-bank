package com.bank.userservice.handlers;

import com.bank.userservice.dto.api.interagration.ExternalResponse;
import com.bank.userservice.dto.event.EventMessage;
import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.service.UserService;
import com.bank.userservice.util.ExternalResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("usercreate")
@RequiredArgsConstructor
public class UserCreateHandler implements Handler<UserCreatePayload> {
    private final UserService userService;

    @Override
    public Class<UserCreatePayload> getPayloadClass() {
        return UserCreatePayload.class;
    }

    @Override
    public ExternalResponse<UserDto> handle(EventMessage<UserCreatePayload> eventMessage, UserCreatePayload payload) {
        log.info("handle: {}", eventMessage);
        return ExternalResponseBuilder.success(userService.createUser(payload));
    }
}
