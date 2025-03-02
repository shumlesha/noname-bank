package ru.patterns.core.atm.deposit.serialization

import java.math.BigDecimal
import java.util.UUID

class DepositRaw(
    val accountId: UUID,
    val amount: BigDecimal
)