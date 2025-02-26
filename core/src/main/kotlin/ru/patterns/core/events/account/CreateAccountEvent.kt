package ru.patterns.core.events.account

import ru.patterns.core.domain.Account

class CreateAccountEvent(
    val account: Account,
    val eventType: String = CREATE_ACCOUNT_EVENT
) {
    companion object {
        const val CREATE_ACCOUNT_EVENT = "CreateAccountEvent"
    }
}