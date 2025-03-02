package ru.patterns.corequery.controller.transaction.serialization

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class FindTransactionResponse(
    val id: UUID,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val transactionTimestamp: LocalDateTime,
    val accountFrom: UUID?,
    val accountTo: UUID?,
    val amount: BigDecimal,
    val clientId: UUID
) : TransactionResponse