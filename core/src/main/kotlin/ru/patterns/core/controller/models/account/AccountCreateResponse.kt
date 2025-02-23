package ru.patterns.core.controller.models.account

import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.AccountNumber
import ru.patterns.core.domain.ClientId

data class AccountCreateResponse(
    val accountId: AccountId,
    val accountNumber: AccountNumber,
    val clientId: ClientId
)