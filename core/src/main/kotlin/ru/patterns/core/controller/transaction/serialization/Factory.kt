package ru.patterns.core.controller.transaction.serialization

import ru.patterns.core.commands.transaction.CreateTransactionCommand
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance

object Factory {
    fun CreateTransactionCommand(createTransactionCommandRaw: CreateTransactionCommandRaw): CreateTransactionCommand =
        CreateTransactionCommand(
            accountFrom = AccountId(createTransactionCommandRaw.accountFrom),
            accountTo = AccountId(createTransactionCommandRaw.accountTo),
            amount = Balance(createTransactionCommandRaw.amount)
        )
}