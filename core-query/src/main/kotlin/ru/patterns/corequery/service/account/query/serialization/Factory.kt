package ru.patterns.corequery.service.account.query.serialization

import ru.patterns.corequery.domain.Account
import ru.patterns.corequery.domain.AccountId
import ru.patterns.corequery.domain.AccountNumber
import ru.patterns.corequery.domain.Balance
import ru.patterns.corequery.domain.ClientId


object Factory {
    fun Account(accountEntity: AccountEntity): Account =
        Account(
            id = AccountId(accountEntity.id!!),
            creationTimestamp = accountEntity.creationTimestamp,
            blockedTimestamp = accountEntity.blockedTimestamp,
            closedTimestamp = accountEntity.closedTimestamp,
            clientId = ClientId(accountEntity.clientId),
            number = AccountNumber(accountEntity.number),
            balance = Balance(accountEntity.balance),
            isCredit = accountEntity.isCredit,
            currency = accountEntity.currency
        )
}