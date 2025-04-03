package ru.patterns.core.domain.commands.account

import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.ClientId


data class CloseAccountCommand(
    val clientId: ClientId,
    val accountId: AccountId
) : AccountCommand