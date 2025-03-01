package ru.patterns.core.service.transaction.command.serialization

import ru.patterns.core.commands.transaction.CreditPaymentTransactionCommand
import ru.patterns.core.domain.MoneyTransfer
import ru.patterns.core.service.transaction.entity.TransactionEntity

object Serializer {
    fun TransactionEntity(moneyTransfer: MoneyTransfer): TransactionEntity =
        TransactionEntity(
            accountFrom = moneyTransfer.accountFrom.id.value,
            accountTo = moneyTransfer.accountTo.id.value,
            amount = moneyTransfer.amount
        )

    fun TransactionEntity(creditPaymentTransactionCommand: CreditPaymentTransactionCommand): TransactionEntity =
        TransactionEntity(
            accountFrom = null,
            accountTo = creditPaymentTransactionCommand.accountId,
            amount = creditPaymentTransactionCommand.amount
        )
}