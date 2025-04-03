package ru.patterns.core.controller.account.serialization

import ru.patterns.core.domain.commands.account.CloseAccountCommand
import ru.patterns.core.domain.commands.account.CreateAccountCommand
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.ClientId
import ru.patterns.core.domain.CurrencyCode
import ru.patterns.core.security.CurrentUser

object Serializer {
    fun CloseAccountCommand(currentUser: CurrentUser, accountCommandRaw: CloseAccountCommandRaw): CloseAccountCommand =
        CloseAccountCommand(
            clientId = ClientId(currentUser.id),
            accountId = AccountId(accountCommandRaw.accountId)
        )

    fun CreateAccountCommand(
        currentUser: CurrentUser,
        accountCommandRaw: CreateAccountCommandRaw
    ): CreateAccountCommand =
        CreateAccountCommand(
            clientId = ClientId(currentUser.id),
            currency = CurrencyCode(accountCommandRaw.currency)
        )
}