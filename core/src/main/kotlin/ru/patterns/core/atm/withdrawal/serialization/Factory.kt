package ru.patterns.core.atm.withdrawal.serialization

import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.Withdrawal

object Factory {
    fun Withdrawal(withdrawalRaw: WithdrawalRaw): Withdrawal =
        Withdrawal(
            accountId = AccountId(withdrawalRaw.accountId),
            amount = Balance(withdrawalRaw.amount)
        )
}