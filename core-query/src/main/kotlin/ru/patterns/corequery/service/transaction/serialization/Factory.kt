package ru.patterns.corequery.service.transaction.serialization

import ru.patterns.corequery.domain.AccountId
import ru.patterns.corequery.domain.Balance
import ru.patterns.corequery.domain.ClientId
import ru.patterns.corequery.domain.Transaction
import ru.patterns.corequery.domain.TransactionId

object Factory {
    fun Transaction(transactionEntity: TransactionEntity): Transaction =
        Transaction(
            id = TransactionId(transactionEntity.id),
            transactionTimestamp = transactionEntity.transactionTimestamp,
            accountFrom = transactionEntity.accountFrom?.let { AccountId(it) },
            accountTo = transactionEntity.accountTo?.let { AccountId(it) },
            amount = Balance(transactionEntity.amount),
            clientId = ClientId(transactionEntity.clientId)
        )
}