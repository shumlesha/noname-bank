package com.bank.userservice.consumer;

import com.bank.userservice.dispatch.EventDispatcher;
import com.bank.userservice.dto.api.interagration.ExternalResponse;
import com.bank.userservice.dto.event.EventMessage;
import com.bank.userservice.util.ExternalResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MQConsumerService {
    private final EventDispatcher eventDispatcher;

    @RabbitListener(
            queues = {"${spring.rabbitmq.queue-config.queue-name}"},
            containerFactory = "rabbitListenerContainerFactory"
    )
    public ExternalResponse<?> receiveMessage(EventMessage<?> eventMessage) {
        log.info("Received message: {}", eventMessage);
        try {
            return eventDispatcher.dispatch(eventMessage);
        } catch (Exception e) {
            return ExternalResponseBuilder.error(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }

    }
}
