package ru.patterns.corequery.controller.account.serialization

data class FindAllAccountsResponse(
    val data: List<FindAccountResponse>
) : AccountResponse

data class FindAllWithPaginationResponse(
    val data: List<FindAccountResponse>,
    val page: Int,
    val pageSize: Int
) : AccountResponse