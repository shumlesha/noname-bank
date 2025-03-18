package ru.patterns.core.service.account.command.serialization

import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.AccountNumber
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.ClientId
import ru.patterns.core.service.account.entity.AccountEntity

object Factory {
    fun Account(accountEntity: AccountEntity): Account =
        Account(
            id = AccountId(accountEntity.id!!),
            creationTimestamp = accountEntity.creationTimestamp,
            blockedTimestamp = accountEntity.blockedTimestamp,
            clientId = ClientId(accountEntity.clientId),
            number = AccountNumber(accountEntity.number),
            balance = Balance(accountEntity.balance),
            isCredit = accountEntity.isCredit,
            closedTimestamp = accountEntity.closedTimestamp,
            currency = accountEntity.currency
        )
}