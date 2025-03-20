package ru.patterns.core.credit.listener.payment

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
import ru.patterns.core.credit.listener.common.sendMessageWithCorrelationData
import ru.patterns.core.credit.listener.payment.serialization.CreditPaymentErrorMessage
import ru.patterns.core.credit.listener.payment.serialization.CreditPaymentResponseMessage
import ru.patterns.core.credit.listener.payment.serialization.RawMqPaymentMessageParser
import ru.patterns.core.service.transaction.command.TransactionCommandService

@Component
class MqCreditPaymentListener(
    private val rabbitTemplate: RabbitTemplate,
    private val mqProperties: MqProperties,
    private val transactionCommandService: TransactionCommandService,
    private val rabbitMqMessageParser: RawMqPaymentMessageParser,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val paymentResponseRoutingKey = mqProperties.creditPaymentResponse.name

    @RabbitListener(queues = ["\${core.credit.mq.credit-payment-request.name}"])
    fun handleCreditPaymentMessage(rawMessage: Message) {
        Mono.fromCallable {
            log.info(
                "Получено сообщение с correlationId: {}, тело: {}",
                rawMessage.messageProperties.correlationId,
                String(rawMessage.body, Charsets.UTF_8)
            )
            rabbitMqMessageParser.parse(rawMessage.body)
        }
            .flatMap { creditPaymentCommand -> transactionCommandService.payCredit(creditPaymentCommand) }
            .map { creditPaymentResult ->
                objectMapper.writeValueAsString(
                    when (creditPaymentResult) {
                        is TransactionCommandService.CreditPaymentResult.Success ->
                            CreditPaymentResponseMessage(creditPaymentResult.debt)

                        is TransactionCommandService.CreditPaymentResult.Error.AccountNotFound ->
                            CreditPaymentErrorMessage("Счет ${creditPaymentResult.accountId} не найден")

                        is TransactionCommandService.CreditPaymentResult.Error.ZeroBalance ->
                            CreditPaymentErrorMessage("На счете недостаточно средств")

                        is TransactionCommandService.CreditPaymentResult.Error.AccountClosedOrBlocked ->
                            CreditPaymentErrorMessage("Счет ${creditPaymentResult.accountId} закрыт или заблокирован")

                        is TransactionCommandService.CreditPaymentResult.Error.ZeroPayment ->
                            CreditPaymentErrorMessage("Минимальная сумма оплаты кредита равна 1 рублю")

                        is TransactionCommandService.CreditPaymentResult.Error ->
                            CreditPaymentErrorMessage("Во время списания произошла ошибка")
                    }
                )
            }
            .doOnSuccess { log.info("Отвечаем в rabbit сообщением: {}", it) }
            .map { body ->
                rabbitTemplate.sendMessageWithCorrelationData(
                    mqProperties.exchange,
                    paymentResponseRoutingKey,
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