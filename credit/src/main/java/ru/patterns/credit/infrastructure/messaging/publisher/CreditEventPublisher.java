package ru.patterns.credit.infrastructure.messaging.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.PayCreditCommand;
import ru.patterns.credit.configuration.mq.RabbitMQProperties;
import ru.patterns.credit.shared.request.CreateCreditAccountRequest;
import ru.patterns.credit.shared.response.CreateCreditAccountResponse;

@Service
@RequiredArgsConstructor
public class CreditEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    public CreateCreditAccountResponse publishCreditCreation(CreateCreditAccountRequest request) {
        var routingKey = properties.getRoutingKeys().get("creditCreateRequest");
        if (routingKey == null) {
            throw new IllegalStateException("Routing key for credit creation not found");
        }

        return (CreateCreditAccountResponse) rabbitTemplate.convertSendAndReceive(properties.getExchange(), routingKey, request);
    }

    public void publishPaymentRequest(PayCreditCommand command) {
        var routingKey = properties.getRoutingKeys().get("paymentRequest");
        if (routingKey == null) {
            throw new IllegalStateException("Routing key for payment request not found");
        }

        rabbitTemplate.convertAndSend(properties.getExchange(), routingKey, command);
    }
}
