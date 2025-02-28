package ru.patterns.corequery.listener.transaction

import org.springframework.stereotype.Component
import ru.patterns.corequery.domain.Transaction
import ru.patterns.corequery.listener.EventProcessor
import ru.patterns.corequery.listener.RawMessageProcessor

@Component
class TransactionMessageProcessor(
    eventProcessor: EventProcessor<Transaction>,
    messageParser: TransactionMessageParser
) : RawMessageProcessor<Transaction>(
    parser = messageParser,
    eventProcessor = eventProcessor
)