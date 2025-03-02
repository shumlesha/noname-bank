package ru.patterns.core.commands.transaction

import jakarta.validation.constraints.Min
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance

class CreateTransactionCommand(
    val accountFrom: AccountId,
    val accountTo: AccountId,
    @field:Min(value = 0, message = "Баланс для перевода должен быть > 0")
    val amount: Balance
)