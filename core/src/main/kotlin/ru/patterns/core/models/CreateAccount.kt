package ru.patterns.core.models

import java.util.UUID

data class CreateAccount(
    val clientId: UUID,
    val isCredit: Boolean
)