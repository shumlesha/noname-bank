package ru.patterns.atm.domain

data class Deposit(
    val accountId: AccountId,
    val amount: Amount
)

data class Withdrawal(
    val accountId: AccountId,
    val amount: Amount
)