package ru.patterns.core.controller.transaction.serialization

import java.math.BigDecimal
import java.util.UUID

class CreateTransactionCommandRaw(
    val accountFrom: UUID,
    val accountTo: UUID,
    val amount: BigDecimal
)