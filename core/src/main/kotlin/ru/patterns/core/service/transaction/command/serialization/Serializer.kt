package ru.patterns.core.service.transaction.command.serialization

import ru.patterns.core.domain.MoneyTransfer
import ru.patterns.core.service.transaction.entity.TransactionEntity

object Serializer {
    fun TransactionEntity(moneyTransfer: MoneyTransfer): TransactionEntity =
        TransactionEntity(
            accountFrom = moneyTransfer.accountFrom.id.value,
            accountTo = moneyTransfer.accountTo.id.value,
            amount = moneyTransfer.amount
        )
}