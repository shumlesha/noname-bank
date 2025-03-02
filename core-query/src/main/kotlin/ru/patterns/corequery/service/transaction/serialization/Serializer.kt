package ru.patterns.corequery.service.transaction.serialization

import ru.patterns.corequery.domain.Transaction

object Serializer {
    fun TransactionEntity(transaction: Transaction): TransactionEntity =
        TransactionEntity(
            id = transaction.id.value,
            accountFrom = transaction.accountFrom?.value,
            accountTo = transaction.accountTo?.value,
            amount = transaction.amount.value,
            clientId = transaction.clientId.value,
            transactionTimestamp = transaction.transactionTimestamp,
        )
}