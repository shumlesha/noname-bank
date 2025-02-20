package ru.patterns.core.events

import ru.patterns.core.domain.Account

data class BlockAccountEvent(
    override val payload: Account
) : AbstractEvent(
    eventType = EventType.ACCOUNT_BLOCK,
    payload = payload,
)