package ru.patterns.corequery.listener.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import ru.patterns.corequery.domain.Transaction
import ru.patterns.corequery.listener.KafkaMessageParser

@Component
class TransactionMessageParser(objectMapper: ObjectMapper) : KafkaMessageParser<Transaction>(
    objectMapper = objectMapper,
    clazz = Transaction::class.java
)