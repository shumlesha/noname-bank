package ru.patterns.core.controller.models.account

import com.fasterxml.jackson.annotation.JsonFormat
import ru.patterns.core.controller.models.ApiResponse
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.AccountNumber
import ru.patterns.core.domain.ClientId
import java.time.LocalDateTime

data class AccountCloseResponse(
    val accountId: AccountId,
    val clientId: ClientId,
    val accountNumber: AccountNumber,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val closedTimestamp: LocalDateTime?
) : ApiResponse