package ru.patterns.core.atm.deposit

import org.springframework.stereotype.Component
import ru.patterns.core.atm.EventProcessor
import ru.patterns.core.atm.RawMessageProcessor
import ru.patterns.core.atm.deposit.serialization.DepositRaw

@Component
class DepositMessageProcessor(
    eventProcessor: EventProcessor<DepositRaw>,
    messageParser: DepositMessageParser
) : RawMessageProcessor<DepositRaw>(
    parser = messageParser,
    eventProcessor = eventProcessor
)