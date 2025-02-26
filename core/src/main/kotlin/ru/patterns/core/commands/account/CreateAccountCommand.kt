package ru.patterns.core.commands.account

import java.util.UUID

data class CreateAccountCommand(
    val clientId: UUID,
    val isCredit: Boolean
) : AccountCommand