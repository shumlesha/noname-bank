package ru.patterns.core.atm.withdrawal.serialization

import java.math.BigDecimal
import java.util.UUID

data class WithdrawalRaw(
    val accountId: UUID,
    val amount: BigDecimal
)