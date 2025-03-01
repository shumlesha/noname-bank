package ru.patterns.core.credit.listener.common

import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate

fun Message(body: String, correlationId: String): Message {
    val messageProps = MessageProperties()
    messageProps.contentType = "application/json"
    messageProps.correlationId = correlationId

    return Message(body.toByteArray(), messageProps)
}

fun RabbitTemplate.sendMessageWithCorrelationData(
    exchange: String,
    routingKey: String,
    body: String,
    correlationId: String
) {
    val responseMessage = Message(body, correlationId)
    val correlationData = CorrelationData(correlationId)

    this.send(exchange, routingKey, responseMessage, correlationData)
}