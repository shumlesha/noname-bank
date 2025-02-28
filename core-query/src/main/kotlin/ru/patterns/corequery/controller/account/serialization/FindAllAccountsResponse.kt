package ru.patterns.corequery.controller.account.serialization

data class FindAllAccountsResponse(
    val data: List<FindAccountResponse>
): AccountResponse