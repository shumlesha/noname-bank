package ru.patterns.corequery.service.account.query.serialization

import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.AccountNumber
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.ClientId


object Factory {
    fun Account(accountEntity: AccountEntity): Account =
        Account(
            id = AccountId(accountEntity.id!!),
            creationTimestamp = accountEntity.creationTimestamp,
            blockedTimestamp = accountEntity.blockedTimestamp,
            closedTimestamp = accountEntity.closedTimestamp,
            clientId = ClientId(accountEntity.id),
            number = AccountNumber(accountEntity.number),
            balance = Balance(accountEntity.balance),
            isCredit = accountEntity.isCredit
        )
}