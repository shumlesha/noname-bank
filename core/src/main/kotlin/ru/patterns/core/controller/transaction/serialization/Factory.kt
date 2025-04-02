package ru.patterns.core.controller.transaction.serialization

import ru.patterns.core.commands.transaction.CreateTransactionCommand
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.ClientId
import ru.patterns.core.security.CurrentUser
import ru.patterns.core.service.account.MasterAccountInitializer.Companion.BANK_ID

object Factory {
    fun CreateTransactionCommand(
        currentUser: CurrentUser,
        createTransactionCommandRaw: CreateTransactionCommandRaw
    ): CreateTransactionCommand =
        CreateTransactionCommand(
            ownerId = ClientId(currentUser.id),
            accountFrom = AccountId(createTransactionCommandRaw.accountFrom),
            accountTo = AccountId(createTransactionCommandRaw.accountTo),
            amount = Balance(createTransactionCommandRaw.amount)
        )

    fun CreateCreditTransactionCommand(
        createTransactionCommandRaw: CreateTransactionCommandRaw
    ): CreateTransactionCommand =
        CreateTransactionCommand(
            ownerId = ClientId(BANK_ID),
            accountFrom = AccountId(createTransactionCommandRaw.accountFrom),
            accountTo = AccountId(createTransactionCommandRaw.accountTo),
            amount = Balance(createTransactionCommandRaw.amount)
        )
}