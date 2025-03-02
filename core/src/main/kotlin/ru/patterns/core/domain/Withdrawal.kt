package ru.patterns.core.domain

data class Withdrawal(
    val accountId: AccountId,
    val amount: Balance
)