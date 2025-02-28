package ru.patterns.corequery.controller.account.serialization

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class FindAccountResponse(
    val id: UUID,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val blockedTimestamp: LocalDateTime?,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val closedTimestamp: LocalDateTime?,
    val clientId: UUID,
    val number: String,
    val balance: BigDecimal,
    val isCredit: Boolean
) : AccountResponse