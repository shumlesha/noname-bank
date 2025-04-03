package ru.patterns.core.credit.listener.account.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import ru.patterns.core.domain.commands.account.CreateCreditAccountCommand
import ru.patterns.core.credit.MqMessageParser

@Component
class RawMqCreditMessageParser(
    objectMapper: ObjectMapper
) : MqMessageParser<CreateCreditAccountCommand>(
    objectMapper = objectMapper,
    clazz = CreateCreditAccountCommand::class.java
)