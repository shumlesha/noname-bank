package ru.patterns.core.controller.account.serialization

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.UUID

sealed interface ApiResponse

data class ErrorResponse(
    val message: String,
    val statusCode: Int
) : ApiResponse

data class AccountCreateResponse(
    val accountId: UUID,
    val accountNumber: String,
    val clientId: UUID
) : ApiResponse

data class AccountCloseResponse(
    val accountId: UUID,
    val clientId: UUID,
    val accountNumber: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val closedTimestamp: LocalDateTime?
) : ApiResponse