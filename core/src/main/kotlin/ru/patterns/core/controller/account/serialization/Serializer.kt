package ru.patterns.core.controller.account.serialization

import ru.patterns.core.commands.account.CloseAccountCommand
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.ClientId
import ru.patterns.core.security.CurrentUser

object Serializer {
    fun CloseAccountCommand(currentUser: CurrentUser, accountCommandRaw: CloseAccountCommandRaw) : CloseAccountCommand =
        CloseAccountCommand(
            clientId = ClientId(currentUser.id),
            accountId = AccountId(accountCommandRaw.accountId)
        )
}