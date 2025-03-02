package ru.patterns.atm.controller.serialization

import ru.patterns.atm.domain.AccountId
import ru.patterns.atm.domain.Amount
import ru.patterns.atm.domain.Deposit
import ru.patterns.atm.domain.Withdrawal

object Factory {
    fun Deposit(depositRaw: DepositRaw): Deposit =
        Deposit(
            accountId = AccountId(depositRaw.accountId),
            amount = Amount(depositRaw.amount)
        )

    fun Withdrawal(withdrawalRaw: WithdrawalRaw): Withdrawal =
        Withdrawal(
            accountId = AccountId(withdrawalRaw.accountId),
            amount = Amount(withdrawalRaw.amount)
        )
}