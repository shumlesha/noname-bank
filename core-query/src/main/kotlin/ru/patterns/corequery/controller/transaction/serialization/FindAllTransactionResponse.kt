package ru.patterns.corequery.controller.transaction.serialization

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class FindAllTransactionResponse(
    val transactions: List<ShortTransactionRaw>
) : TransactionResponse

data class ShortTransactionRaw(
    val id: UUID,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val transactionTimestamp: LocalDateTime,
    val clientId: UUID,
    val amount: BigDecimal
)