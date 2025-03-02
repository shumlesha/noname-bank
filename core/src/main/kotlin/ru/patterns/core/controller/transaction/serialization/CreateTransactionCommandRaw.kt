package ru.patterns.core.controller.transaction.serialization

import jakarta.validation.constraints.Min
import java.math.BigDecimal
import java.util.UUID

class CreateTransactionCommandRaw(
    val accountFrom: UUID,
    val accountTo: UUID,
    @field:Min(value = 0, message = "Баланс для перевода должен быть > 0")
    val amount: BigDecimal
)