package ru.patterns.corequery.controller.transaction.serialization

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class FindTransactionResponse(
    val id: UUID,
    val transactionTimestamp: LocalDateTime,
    val accountFrom: UUID?,
    val accountTo: UUID,
    val amount: BigDecimal,
    val clientId: UUID
) : TransactionResponse