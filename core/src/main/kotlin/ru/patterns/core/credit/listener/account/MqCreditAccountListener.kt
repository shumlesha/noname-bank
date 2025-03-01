package ru.patterns.core.credit.listener.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.config.MqProperties
import ru.patterns.core.credit.listener.account.serialization.CreateCreditErrorResponse
import ru.patterns.core.credit.listener.account.serialization.CreateCreditResponseMessage
import ru.patterns.core.credit.listener.account.serialization.RawMqCreditMessageParser
import ru.patterns.core.credit.listener.common.sendMessageWithCorrelationData
import ru.patterns.core.service.account.command.AccountCommandService

@Component
class MqCreditAccountListener(
    private val rabbitTemplate: RabbitTemplate,
    private val mqProperties: MqProperties,
    private val accountCommandService: AccountCommandService,
    private val rabbitMqMessageParser: RawMqCreditMessageParser,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val accountResponseRoutingKey = mqProperties.creditCreateResponse.name

    @RabbitListener(queues = ["\${core.credit.mq.credit-create-request.name}"])
    fun handleCreateAccountMessage(rawMessage: Message) {
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
            .map { body ->
                rabbitTemplate.sendMessageWithCorrelationData(
                    mqProperties.exchange,
                    accountResponseRoutingKey,
                    body,
                    rawMessage.messageProperties.correlationId
                )
            }
            .doOnError { error -> log.error("При обработке сообщения из rabbit произошла ошибка", error) }
            .onErrorResume { Unit.toMono() }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}