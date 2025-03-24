package ru.patterns.core.atm.withdrawal

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import ru.patterns.core.atm.withdrawal.serialization.WithdrawalRaw
import ru.patterns.core.service.kafka.listener.KafkaMessageParser

@Component
class WithdrawalMessageParser(objectMapper: ObjectMapper) : KafkaMessageParser<WithdrawalRaw>(
    objectMapper = objectMapper,
    clazz = WithdrawalRaw::class.java
)