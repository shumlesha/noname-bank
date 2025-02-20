package ru.patterns.core.events

import ru.patterns.core.domain.Transaction

data class CreateTransactionEvent(
    override val payload: Transaction
) : AbstractEvent(
    eventType = EventType.TRANSACTION_CREATE,
    payload = payload,
)