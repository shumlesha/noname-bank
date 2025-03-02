package ru.patterns.core.domain

data class Deposit(
    val accountId: AccountId,
    val amount: Balance
)