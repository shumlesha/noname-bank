package ru.patterns.core.service.kafka.serialization

import ru.patterns.core.domain.ClientId
import ru.patterns.core.domain.Transaction

object Serializer {
    fun TransactionQueryEntity(clientId: ClientId, transaction: Transaction): TransactionQueryEntity =
        TransactionQueryEntity(
            id = transaction.id,
            transactionTimestamp = transaction.transactionTimestamp,
            accountFrom = transaction.accountFrom,
            accountTo = transaction.accountTo,
            amount = transaction.amount,
            clientId = clientId.value
        )
}