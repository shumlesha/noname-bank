package ru.patterns.core.atm.withdrawal

import org.springframework.stereotype.Component
import ru.patterns.core.atm.EventProcessor
import ru.patterns.core.atm.RawMessageProcessor
import ru.patterns.core.atm.withdrawal.serialization.WithdrawalRaw

@Component
class WithdrawalMessageProcessor(
    eventProcessor: EventProcessor<WithdrawalRaw>,
    messageParser: WithdrawalMessageParser
) : RawMessageProcessor<WithdrawalRaw>(
    parser = messageParser,
    eventProcessor = eventProcessor
)