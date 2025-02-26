package com.bank.userservice.handlers;

import com.bank.userservice.dto.event.EventMessage;
import com.bank.userservice.dto.event.Payload;

public interface Handler<T extends Payload> {
    Class<T> getPayloadClass();

    void handle(EventMessage<T> eventMessage, T payload);
}
