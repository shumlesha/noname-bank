package ru.patterns.core.service.transaction.command.serialization

import ru.patterns.core.commands.transaction.CreditPaymentTransactionCommand
import ru.patterns.core.domain.Deposit
import ru.patterns.core.domain.MoneyTransfer
import ru.patterns.core.domain.Withdrawal
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
            accountFrom = creditPaymentTransactionCommand.accountId,
            accountTo = null,
            amount = creditPaymentTransactionCommand.amount
        )

    fun TransactionEntity(deposit: Deposit): TransactionEntity =
        TransactionEntity(
            accountFrom = null,
            accountTo = deposit.accountId.value,
            amount = deposit.amount.value
        )

    fun TransactionEntity(withdrawal: Withdrawal): TransactionEntity =
        TransactionEntity(
            accountFrom = withdrawal.accountId.value,
            accountTo = null,
            amount = withdrawal.amount.value
        )
}