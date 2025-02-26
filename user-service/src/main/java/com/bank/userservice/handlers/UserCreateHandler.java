package com.bank.userservice.handlers;

import com.bank.userservice.dto.event.EventMessage;
import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.service.UserService;
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
    public void handle(EventMessage<UserCreatePayload> eventMessage, UserCreatePayload payload) {
        log.info("handle: {}", eventMessage);
        userService.createUser(payload);
    }
}
