package ru.patterns.core.credit.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.config.MqProperties
import ru.patterns.core.credit.listener.serialization.CreateCreditErrorResponse
import ru.patterns.core.credit.listener.serialization.RawMqMessageParser
import ru.patterns.core.credit.listener.serialization.CreateCreditResponseMessage
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
    fun handleMessage(message: Message) {
        Mono.fromCallable {
            val rawMessage = String(message.body, Charsets.UTF_8)
            val correlationId = message.messageProperties.correlationId

            log.info("Получено сообщение: {}, correlationId: {}", rawMessage, correlationId)

            val parsedMessage = rabbitMqMessageParser.parse(rawMessage)
            Pair(parsedMessage, correlationId)
        }
            .flatMap { (createCreditCommand, correlationId) ->
                accountCommandService.createCreditAccount(createCreditCommand)
                    .map { createAccountResult -> Pair(createAccountResult, correlationId) }
            }
            .map { (createAccountResult, correlationId) ->
                val responseBody = objectMapper.writeValueAsString(
                    when (createAccountResult) {
                        is AccountCommandService.CreateAccountResult.Success ->
                            CreateCreditResponseMessage(createAccountResult.account)
                        is AccountCommandService.CreateAccountResult.Error ->
                            CreateCreditErrorResponse("Не удалось создать кредитный счет")
                    }
                )

                log.info("Отправляем ответ в RabbitMQ: {}, correlationId: {}", responseBody, correlationId)

                val responseMessage = MessageBuilder
                    .withBody(responseBody.toByteArray(Charsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setCorrelationId(correlationId)
                    .build()

                rabbitTemplate.send(mqProperties.exchange, responseRoutingKey, responseMessage)
            }
            .doOnError { error -> log.error("Ошибка при обработке сообщения из RabbitMQ", error) }
            .onErrorResume { Unit.toMono() }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
