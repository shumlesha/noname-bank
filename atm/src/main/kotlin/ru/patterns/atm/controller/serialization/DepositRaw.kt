package ru.patterns.atm.controller.serialization

import jakarta.validation.constraints.Min
import java.math.BigDecimal
import java.util.UUID

data class DepositRaw(
    val accountId: UUID,
    @field:Min(value = 0, message = "Баланс для начисления должен быть > 0")
    val amount: BigDecimal
)