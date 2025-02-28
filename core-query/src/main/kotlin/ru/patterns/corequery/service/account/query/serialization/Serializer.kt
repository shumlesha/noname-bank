package ru.patterns.corequery.service.account.query.serialization

import ru.patterns.corequery.domain.Account

object Serializer {
    fun AccountEntity(account: Account) =
        AccountEntity(
            id = account.id.value,
            creationTimestamp = account.creationTimestamp,
            closedTimestamp = account.closedTimestamp,
            blockedTimestamp = account.blockedTimestamp,
            clientId = account.clientId.value,
            number = account.number.value,
            isCredit = account.isCredit,
            balance = account.balance.value
        )
}