package ru.patterns.core.service.account.command.serialization

import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.commands.account.CreateCreditAccountCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.service.account.entity.AccountEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random

object Serializer {
    fun AccountEntity(createAccountCommand: CreateAccountCommand) =
        AccountEntity(
            id = null,
            clientId = createAccountCommand.clientId,
            number = generate16DigitNumber(),
            isCredit = false,
            creationTimestamp = LocalDateTime.now(),
            blockedTimestamp = null,
            closedTimestamp = null,
            balance = BigDecimal.ZERO
        )

    fun AccountEntity(createCreditAccountCommand: CreateCreditAccountCommand) =
        AccountEntity(
            id = null,
            clientId = createCreditAccountCommand.clientId,
            number = generate16DigitNumber(),
            isCredit = true,
            creationTimestamp = LocalDateTime.now(),
            blockedTimestamp = null,
            closedTimestamp = null,
            balance = BigDecimal.ZERO
        )

    fun AccountEntity(account: Account) =
        AccountEntity(
            id = account.id.value,
            clientId = account.clientId.value,
            number = account.number.value,
            isCredit = account.isCredit,
            creationTimestamp = account.creationTimestamp,
            blockedTimestamp = account.blockedTimestamp,
            closedTimestamp = account.closedTimestamp,
            balance = account.balance.value,
        )

    private fun generate16DigitNumber(): String {
        val random = Random(System.currentTimeMillis())
        val number = StringBuilder()

        number.append(random.nextInt(1, 10))

        for (i in 1 until 16) {
            number.append(random.nextInt(0, 10))
        }

        return number.toString()
    }
}