package ru.patterns.core.atm.deposit

import org.springframework.stereotype.Component
import ru.patterns.core.atm.deposit.serialization.DepositRaw
import ru.patterns.core.service.kafka.listener.EventProcessor
import ru.patterns.core.service.kafka.listener.RawMessageProcessor

@Component
class DepositMessageProcessor(
    eventProcessor: EventProcessor<DepositRaw>,
    messageParser: DepositMessageParser
) : RawMessageProcessor<DepositRaw>(
    parser = messageParser,
    eventProcessor = eventProcessor
)