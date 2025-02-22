package ru.patterns.core.domain

import java.math.BigDecimal

data class Balance(
    val balance: BigDecimal
)

data class AccountIdentification(
    val clientId: ClientId,
    val accountId: AccountId
)