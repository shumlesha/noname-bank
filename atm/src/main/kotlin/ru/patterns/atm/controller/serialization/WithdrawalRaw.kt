package ru.patterns.atm.controller.serialization

import jakarta.validation.constraints.Min
import java.math.BigDecimal
import java.util.UUID

data class WithdrawalRaw(
    val accountId: UUID,
    @field:Min(value = 0, message = "Баланс для списания должен быть > 0")
    val amount: BigDecimal
)