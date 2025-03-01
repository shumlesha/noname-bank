package ru.patterns.corequery.controller.account.serialization

import ru.patterns.corequery.domain.GetAccountsWithPagination
import ru.patterns.corequery.domain.PaginationOffset
import ru.patterns.corequery.domain.PaginationSize

object Factory {
    fun GetAccountsWithPagination(getAccountsWithPaginationRaw: GetAccountsWithPaginationRaw): GetAccountsWithPagination =
        GetAccountsWithPagination(
            size = PaginationSize(getAccountsWithPaginationRaw.size),
            offset = PaginationOffset(getAccountsWithPaginationRaw.offset)
        )
}