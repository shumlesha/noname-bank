package ru.patterns.core.service.transaction.command.serialization

import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.Transaction
import ru.patterns.core.service.transaction.entity.TransactionEntity

object Factory {
    fun Transaction(transactionEntity: TransactionEntity): Transaction =
        Transaction(
            id = transactionEntity.id!!,
            transactionTimestamp = transactionEntity.transactionTimestamp,
            accountFrom = transactionEntity.accountFrom,
            accountTo = transactionEntity.accountTo,
            amount = Balance(transactionEntity.amount)
        )
}