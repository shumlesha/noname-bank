package com.bank.authservice.service.impl;

import com.bank.authservice.config.rabbit.RabbitMQProperties;
import com.bank.authservice.dto.intergration.response.ResponseWrapper;
import com.bank.authservice.dto.intergration.event.EventMessage;
import com.bank.authservice.dto.intergration.exception.IntegrationException;
import com.bank.authservice.dto.user.GetUserRequest;
import com.bank.authservice.dto.user.SaveUserRequest;
import com.bank.authservice.dto.user.UserDto;
import com.bank.authservice.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceClientImpl implements UserServiceClient {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    @Override
    public UserDto saveUser(SaveUserRequest saveUserRequest) {
        EventMessage<SaveUserRequest> eventMessage = new EventMessage<>("USER_CREATE", Instant.now(), saveUserRequest);

        ResponseWrapper<UserDto> response = rabbitTemplate.convertSendAndReceiveAsType(
                properties.getQueueConfig().getExchange(),
                properties.getQueueConfig().getRoutingKey(),
                eventMessage,
                new ParameterizedTypeReference<>() {
                }
        );

        return Optional.ofNullable(response)
                .orElseThrow(() -> new IntegrationException("Failed to save user"))
                .getOrThrow();
    }

    @Override
    public UserDto getUserByEmail(GetUserRequest getUserRequest) {
        EventMessage<GetUserRequest> eventMessage =
                new EventMessage<>("USER_GET", Instant.now(), getUserRequest);
        ResponseWrapper<UserDto> response = rabbitTemplate.convertSendAndReceiveAsType(
                properties.getQueueConfig().getExchange(),
                properties.getQueueConfig().getRoutingKey(),
                eventMessage,
                new ParameterizedTypeReference<>() {
                }
        );

        return Optional.ofNullable(response)
                .orElseThrow(() -> new IntegrationException("Failed to get user"))
                .getOrThrow();
    }
}
