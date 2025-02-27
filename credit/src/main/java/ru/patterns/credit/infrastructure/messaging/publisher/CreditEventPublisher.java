package ru.patterns.credit.infrastructure.messaging.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.patterns.credit.configuration.mq.RabbitMQProperties;
import ru.patterns.credit.shared.request.CreateCreditAccountRequest;
import ru.patterns.credit.shared.request.PayCreditRequest;
import ru.patterns.credit.utils.ObjectMapperUtils;

import java.util.UUID;

@Service
@Slf4j
public class CreditEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    public CreditEventPublisher(
            @Qualifier("asd") RabbitTemplate rabbitTemplate,
            RabbitMQProperties properties
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    public Message publishCreditCreation(CreateCreditAccountRequest request) {
        var routingKey = properties.getRoutingKeys().get("creditCreateRequest");
        if (routingKey == null) {
            throw new IllegalStateException("Routing key для создания кредита не найден");
        }

        var messageProperties = new MessageProperties();
        messageProperties.setReplyTo(properties.getQueues().get("creditCreateResponse"));
        messageProperties.setCorrelationId(UUID.randomUUID().toString());

        var message = new Message(ObjectMapperUtils.writeValueAsAString(request).getBytes(), messageProperties);

        return rabbitTemplate.sendAndReceive(
                properties.getExchange(),
                routingKey,
                message
        );
    }

    public void publishPaymentRequest(PayCreditRequest request) {
        var routingKey = properties.getRoutingKeys().get("paymentRequest");
        if (routingKey == null) {
            throw new IllegalStateException("Routing key для оплаты кредита не найден");
        }

        rabbitTemplate.convertAndSend(properties.getExchange(), routingKey, request);
    }
}
