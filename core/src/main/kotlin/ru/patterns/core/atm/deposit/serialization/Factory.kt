package ru.patterns.core.atm.deposit.serialization

import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.Deposit

object Factory {
    fun Deposit(depositRaw: DepositRaw): Deposit =
        Deposit(
            accountId = AccountId(depositRaw.accountId),
            amount = Balance(depositRaw.amount)
        )
}