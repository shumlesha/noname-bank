package com.bank.userservice.dispatch;

import com.bank.userservice.dto.api.interagration.ExternalResponse;
import com.bank.userservice.dto.event.EventMessage;
import com.bank.userservice.dto.event.Payload;
import com.bank.userservice.handlers.Handler;
import com.bank.userservice.util.StringFormatter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ExtensionMethod({StringFormatter.class})
@RequiredArgsConstructor
public class EventDispatcher {
    private final Map<String, Handler<? extends Payload>> handlers;

    public ExternalResponse<?> dispatch(EventMessage<?> eventMessage) {
        String eventType = eventMessage.getEventType();
        String handlerName = eventType.formatToHandlerName();

        Handler<?> handler = handlers.get(handlerName);

        if (handler == null) {
            throw new IllegalArgumentException("No handler found for event type: " + eventType);
        }

        if (!handler.getPayloadClass().isInstance(eventMessage.getPayload())) {
            throw new IllegalArgumentException("Payload type mismatch for event type: " + eventType);
        }

        return dispatchWithPayload(handler, eventMessage);
    }

    @SuppressWarnings("unchecked")
    private <T extends Payload> ExternalResponse<?> dispatchWithPayload(Handler<T> handler,
                                                                        EventMessage<?> eventMessage) {
        T payload = handler.getPayloadClass().cast(eventMessage.getPayload());

        return handler.handle((EventMessage<T>) eventMessage, payload);
    }
}
