package ru.patterns.core.commands.account

import ru.patterns.core.domain.ClientId
import ru.patterns.core.domain.CurrencyCode

data class CreateAccountCommand(
    val clientId: ClientId,
    val currency: CurrencyCode
) : AccountCommand