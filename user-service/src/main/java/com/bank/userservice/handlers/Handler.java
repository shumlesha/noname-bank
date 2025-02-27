package com.bank.userservice.handlers;

import com.bank.userservice.dto.api.interagration.ExternalResponse;
import com.bank.userservice.dto.event.EventMessage;
import com.bank.userservice.dto.event.Payload;

public interface Handler<T extends Payload> {
    Class<T> getPayloadClass();

    ExternalResponse<?> handle(EventMessage<T> eventMessage, T payload);
}
