package ru.patterns.core.commands.transaction

import java.math.BigDecimal
import java.util.UUID

data class CreditPaymentTransactionCommand(
    val accountId: UUID,
    val amount: BigDecimal
)