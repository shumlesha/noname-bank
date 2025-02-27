package ru.patterns.core.commands.account

import java.math.BigDecimal
import java.util.UUID

class CreateCreditAccountCommand(
    val clientId: UUID,
    val amount: BigDecimal
)