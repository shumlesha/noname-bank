package ru.patterns.corequery.controller.transaction.serialization

import ru.patterns.corequery.domain.AccountId

object Factory {
    fun AccountId(accountIdRaw: AccountIdRaw): AccountId = AccountId(accountIdRaw.accountId)
}