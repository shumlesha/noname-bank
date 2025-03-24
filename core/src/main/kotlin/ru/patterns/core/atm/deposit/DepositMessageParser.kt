package ru.patterns.core.atm.deposit

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import ru.patterns.core.atm.deposit.serialization.DepositRaw
import ru.patterns.core.service.kafka.listener.KafkaMessageParser

@Component
class DepositMessageParser(objectMapper: ObjectMapper) : KafkaMessageParser<DepositRaw>(
    objectMapper = objectMapper,
    clazz = DepositRaw::class.java
)