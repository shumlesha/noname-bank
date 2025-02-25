package ru.patterns.credit.infrastructure.messaging.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.patterns.credit.configuration.mq.RabbitMQProperties;
import ru.patterns.credit.shared.request.CreateCreditAccountRequest;
import ru.patterns.credit.shared.request.PayCreditRequest;
import ru.patterns.credit.shared.response.CreateCreditAccountResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    public CreateCreditAccountResponse publishCreditCreation(CreateCreditAccountRequest request) {
        var routingKey = properties.getRoutingKeys().get("creditCreateRequest");
        if (routingKey == null) {
            throw new IllegalStateException("Routing key для создания кредита не найден");
        }

        log.warn(request.toString());
        return (CreateCreditAccountResponse) rabbitTemplate.convertSendAndReceive(properties.getExchange(), routingKey, request);
    }

    public void publishPaymentRequest(PayCreditRequest request) {
        var routingKey = properties.getRoutingKeys().get("paymentRequest");
        if (routingKey == null) {
            throw new IllegalStateException("Routing key для оплаты кредита не найден");
        }

        rabbitTemplate.convertAndSend(properties.getExchange(), routingKey, request);
    }
}
