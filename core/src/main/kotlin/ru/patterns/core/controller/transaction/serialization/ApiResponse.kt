package ru.patterns.core.controller.transaction.serialization

import java.math.BigDecimal
import java.util.UUID

sealed interface ApiResponse

data class ErrorResponse(
    val message: String,
    val statusCode: Int
) : ApiResponse

data class CreateTransactionResponse(
    val transactionId: UUID,
    val accountTo: UUID,
    val accountFrom: UUID,
    val amount: BigDecimal
): ApiResponse