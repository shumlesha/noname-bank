package ru.patterns.core.domain.commands.transaction

import jakarta.validation.constraints.Min
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.ClientId

class CreateTransactionCommand(
    val ownerId: ClientId,
    val accountFrom: AccountId,
    val accountTo: AccountId,
    @field:Min(value = 0, message = "Баланс для перевода должен быть > 0")
    val amount: Balance
)