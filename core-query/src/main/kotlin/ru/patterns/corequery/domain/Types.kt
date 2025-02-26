package ru.patterns.core.domain

data class AccountIdentification(
    val clientId: ClientId,
    val accountId: AccountId
)

data class ClientIdentification(
    val clientId: ClientId
)