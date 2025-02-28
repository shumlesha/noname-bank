package ru.patterns.corequery.listener.account

import org.springframework.stereotype.Component
import ru.patterns.corequery.domain.Account
import ru.patterns.corequery.listener.EventProcessor
import ru.patterns.corequery.listener.RawMessageProcessor

@Component
class AccountMessageProcessor(
    eventProcessor: EventProcessor<Account>,
    accountMessageParser: AccountMessageParser
) : RawMessageProcessor<Account>(
    parser = accountMessageParser,
    eventProcessor = eventProcessor
)