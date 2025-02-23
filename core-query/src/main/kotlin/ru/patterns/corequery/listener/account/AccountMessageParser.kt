package ru.patterns.corequery.listener.account

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import ru.patterns.core.domain.Account
import ru.patterns.corequery.listener.KafkaMessageParser

@Component
class AccountMessageParser(objectMapper: ObjectMapper) : KafkaMessageParser<Account>(
    objectMapper = objectMapper,
    clazz = Account::class.java
)