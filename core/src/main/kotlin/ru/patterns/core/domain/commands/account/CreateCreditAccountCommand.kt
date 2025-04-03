package ru.patterns.core.domain.commands.account

import java.math.BigDecimal
import java.util.UUID

class CreateCreditAccountCommand(
    val clientId: UUID,
    val amount: BigDecimal
)