package ru.patterns.core.domain

import java.math.BigDecimal

data class AccountIdentification(
    val clientId: ClientId,
    val accountId: AccountId
)

data class MoneyTransfer(
    val accountFrom: Account,
    val accountTo: Account,
    val amount: BigDecimal
)