package ru.patterns.core.service.account.query.serialization

import ru.patterns.core.domain.Account
import ru.patterns.core.service.account.entity.AccountEntity

object Factory {
    fun Account(accountEntity: AccountEntity): Account =
        Account(
            id = accountEntity.id!!,
            creationTimestamp = accountEntity.creationTimestamp,
            blockedTimestamp = accountEntity.blockedTimestamp,
            clientId = accountEntity.clientId,
            number = accountEntity.number,
            balance = accountEntity.balance,
            isCredit = accountEntity.isCredit
        )
}