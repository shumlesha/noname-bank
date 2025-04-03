package ru.patterns.core.credit.listener.payment.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import ru.patterns.core.domain.commands.transaction.CreditPaymentTransactionCommand
import ru.patterns.core.credit.MqMessageParser

@Component
class RawMqPaymentMessageParser(
    objectMapper: ObjectMapper
) : MqMessageParser<CreditPaymentTransactionCommand>(
    objectMapper = objectMapper,
    clazz = CreditPaymentTransactionCommand::class.java
)