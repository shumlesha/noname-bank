package ru.patterns.corequery.controller.models.account

import ru.patterns.corequery.controller.models.ApiResponse

data class FindAllAccountsResponse(
    val data: List<FindAccountResponse>
): ApiResponse