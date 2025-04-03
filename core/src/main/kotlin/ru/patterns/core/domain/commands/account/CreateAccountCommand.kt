package ru.patterns.core.domain.commands.account

import ru.patterns.core.domain.ClientId
import ru.patterns.core.domain.CurrencyCode

data class CreateAccountCommand(
    val clientId: ClientId,
    val currency: CurrencyCode
) : AccountCommand