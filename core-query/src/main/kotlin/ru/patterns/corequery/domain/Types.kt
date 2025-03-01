package ru.patterns.corequery.domain

data class AccountIdentification(
    val clientId: ClientId,
    val accountId: AccountId
)

data class ClientIdentification(
    val clientId: ClientId
)

data class TransactionInfo(
    val clientId: ClientId,
    val transactionId: TransactionId
)

data class GetAccountsWithPagination(
    val size: PaginationSize,
    val offset: PaginationOffset,
)