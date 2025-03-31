package ru.patterns.core.controller.account.serialization

import java.util.UUID

data class CloseAccountCommandRaw(
    val accountId: UUID
)