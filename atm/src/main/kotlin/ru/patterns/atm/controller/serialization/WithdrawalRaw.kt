package ru.patterns.atm.controller.serialization

import java.math.BigDecimal
import java.util.UUID

data class WithdrawalRaw(
    val accountId: UUID,
    val amount: BigDecimal
)