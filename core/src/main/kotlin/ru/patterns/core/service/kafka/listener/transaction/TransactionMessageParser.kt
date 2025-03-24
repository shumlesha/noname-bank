package ru.patterns.core.service.kafka.listener.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import ru.patterns.core.controller.transaction.serialization.CreateTransactionCommandRaw
import ru.patterns.core.service.kafka.listener.KafkaMessageParser

@Component
class TransactionMessageParser(objectMapper: ObjectMapper) : KafkaMessageParser<CreateTransactionCommandRaw>(
    objectMapper = objectMapper,
    clazz = CreateTransactionCommandRaw::class.java
)