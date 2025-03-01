package ru.patterns.corequery.controller.account.serialization

data class GetAccountsWithPaginationRaw(
    val size: Int,
    val offset: Int
)