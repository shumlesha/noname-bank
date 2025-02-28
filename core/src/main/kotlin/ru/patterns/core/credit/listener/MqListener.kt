package ru.patterns.core.credit.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.config.MqProperties
import ru.patterns.core.credit.listener.serialization.CreateCreditErrorResponse
import ru.patterns.core.credit.listener.serialization.CreateCreditResponseMessage
import ru.patterns.core.credit.listener.serialization.RawMqMessageParser
import ru.patterns.core.service.account.command.AccountCommandService

@Component
class MqListener(
    private val rabbitTemplate: RabbitTemplate,
    private val mqProperties: MqProperties,
    private val accountCommandService: AccountCommandService,
    private val rabbitMqMessageParser: RawMqMessageParser,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val responseRoutingKey = mqProperties.creditCreateResponse.name

    @RabbitListener(queues = ["\${core.credit.mq.credit-create-request.name}"])
    fun handleMessage(rawMessage: Message) {
        Mono.fromCallable {
            log.info(
                "Получено сообщение с correlationId: {}, тело: {}",
                rawMessage.messageProperties.correlationId,
                rawMessage.body
            )
            rabbitMqMessageParser.parse(rawMessage.body)
        }
            .flatMap { createCreditCommand -> accountCommandService.createCreditAccount(createCreditCommand) }
            .map { createAccountResult ->
                objectMapper.writeValueAsString(
                    when (createAccountResult) {
                        is AccountCommandService.CreateAccountResult.Success ->
                            CreateCreditResponseMessage(createAccountResult.account)

                        is AccountCommandService.CreateAccountResult.Error ->
                            CreateCreditErrorResponse("Не удалось создать кредитный счет")
                    }
                )
            }
            .doOnSuccess { log.info("Отвечаем в rabbit сообщением: {}", it) }
            .map {
                val responseMessage = Message(it, rawMessage.messageProperties.correlationId)
                val correlationData = CorrelationData(rawMessage.messageProperties.correlationId)

                rabbitTemplate.send(mqProperties.exchange, responseRoutingKey, responseMessage, correlationData)
            }
            .doOnError { error -> log.error("При обработке сообщения из rabbit произошла ошибка", error) }
            .onErrorResume { Unit.toMono() }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun Message(body: String, correlationId: String): Message {
        val messageProps = MessageProperties()
        messageProps.contentType = "application/json"
        messageProps.correlationId = correlationId

        return Message(body.toByteArray(), messageProps)
    }
}