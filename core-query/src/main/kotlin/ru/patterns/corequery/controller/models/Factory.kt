package ru.patterns.corequery.controller.models

import ru.patterns.core.domain.Account
import ru.patterns.corequery.controller.models.account.FindAccountResponse
import ru.patterns.corequery.controller.models.account.FindAllAccountsResponse

object Factory {
    fun FindAccountResponse(account: Account) =
        FindAccountResponse(
            id = account.id.value,
            creationTimestamp = account.creationTimestamp,
            blockedTimestamp = account.blockedTimestamp,
            closedTimestamp = account.closedTimestamp,
            clientId = account.clientId.value,
            number = account.number.value,
            balance = account.balance.value,
            isCredit = account.isCredit
        )

    fun FindAllAccountsResponse(accounts: List<Account>) =
        FindAllAccountsResponse(
            data = accounts.map(::FindAccountResponse)
        )
}