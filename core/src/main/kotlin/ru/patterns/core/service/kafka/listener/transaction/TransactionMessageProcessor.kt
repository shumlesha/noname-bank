package ru.patterns.core.service.kafka.listener.transaction

import org.springframework.stereotype.Component
import ru.patterns.core.controller.transaction.serialization.CreateTransactionCommandRaw
import ru.patterns.core.service.kafka.listener.EventProcessor
import ru.patterns.core.service.kafka.listener.RawMessageProcessor

@Component
class TransactionMessageProcessor(
    eventProcessor: EventProcessor<CreateTransactionCommandRaw>,
    messageParser: TransactionMessageParser
) : RawMessageProcessor<CreateTransactionCommandRaw>(
    parser = messageParser,
    eventProcessor = eventProcessor
)