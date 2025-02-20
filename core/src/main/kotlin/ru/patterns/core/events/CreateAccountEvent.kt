package ru.patterns.core.events

import ru.patterns.core.domain.Account

data class CreateAccountEvent(
    override val payload: Account
) : AbstractEvent(
    eventType = EventType.ACCOUNT_CREATE,
    payload = payload,
)