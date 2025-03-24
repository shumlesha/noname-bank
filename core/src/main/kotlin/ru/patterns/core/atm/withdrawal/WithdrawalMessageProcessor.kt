package ru.patterns.core.atm.withdrawal

import org.springframework.stereotype.Component
import ru.patterns.core.atm.withdrawal.serialization.WithdrawalRaw
import ru.patterns.core.service.kafka.listener.EventProcessor
import ru.patterns.core.service.kafka.listener.RawMessageProcessor

@Component
class WithdrawalMessageProcessor(
    eventProcessor: EventProcessor<WithdrawalRaw>,
    messageParser: WithdrawalMessageParser
) : RawMessageProcessor<WithdrawalRaw>(
    parser = messageParser,
    eventProcessor = eventProcessor
)