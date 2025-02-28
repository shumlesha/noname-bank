package com.bank.userservice.handlers;

import com.bank.userservice.dto.api.interagration.ExternalResponse;
import com.bank.userservice.dto.event.EventMessage;
import com.bank.userservice.dto.event.payload.UserGetPayload;
import com.bank.userservice.service.UserService;
import com.bank.userservice.util.ExternalResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userget")
@RequiredArgsConstructor
public class UserGetHandler implements Handler<UserGetPayload> {
    private final UserService userService;

    @Override
    public Class<UserGetPayload> getPayloadClass() {
        return UserGetPayload.class;
    }

    @Override
    public ExternalResponse<?> handle(EventMessage<UserGetPayload> eventMessage, UserGetPayload payload) {
        String email = payload.getEmail();

        return ExternalResponseBuilder.success(userService.getByEmail(email));
    }
}
