package ru.patterns.core.commands.transaction

import jakarta.validation.constraints.Min
import java.math.BigDecimal
import java.util.UUID

class CreateTransactionCommand(
    val accountFrom: UUID,
    val accountTo: UUID,
    @field:Min(value = 0, message = "Баланс для перевода должен быть > 0")
    val amount: BigDecimal
)