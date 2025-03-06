package ru.patterns.core.service.transaction.command

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.commands.transaction.CreateTransactionCommand
import ru.patterns.core.commands.transaction.CreditPaymentTransactionCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.ClientId
import ru.patterns.core.domain.MoneyTransfer
import ru.patterns.core.domain.Transaction
import ru.patterns.core.service.account.repository.AccountRepository
import ru.patterns.core.service.kafka.KafkaEventSender
import ru.patterns.core.service.transaction.command.TransactionCommandService.CreateTransactionResult
import ru.patterns.core.service.transaction.command.TransactionCommandService.CreditPaymentResult
import ru.patterns.core.service.transaction.repository.TransactionRepository
import java.math.BigDecimal
import java.util.UUID

sealed interface TransactionCommandService {
    fun create(createTransactionCommand: CreateTransactionCommand): Mono<CreateTransactionResult>
    fun payCredit(creditPaymentTransactionCommand: CreditPaymentTransactionCommand): Mono<CreditPaymentResult>

    sealed interface CreditPaymentResult {
        data class Success(val debt: BigDecimal) : CreditPaymentResult
        sealed interface Error : CreditPaymentResult {
            data class AccountNotFound(val accountId: UUID) : Error
            data object ZeroBalance : Error
            data class AccountClosedOrBlocked(val accountId: UUID) : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }

    sealed interface CreateTransactionResult {
        data class Success(val transaction: Transaction) : CreateTransactionResult
        sealed interface Error : CreateTransactionResult {
            data class SaveErrorFromRepository(val error: TransactionRepository.SaveTransactionResult.Error) : Error
            data object NotEnoughMoney : Error
            data object SameAccount : Error
            data class AccountNotFound(val accountId: UUID) : Error
            data class UnexpectedError(val cause: Throwable) : Error
            data object AccountClosedOrBlocked : Error
        }
    }
}

@Component
class TransactionCommandServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val kafkaEventSender: KafkaEventSender
) : TransactionCommandService {
    private val log = LoggerFactory.getLogger(this::class.java)!!

    override fun create(createTransactionCommand: CreateTransactionCommand): Mono<CreateTransactionResult> =
        if (createTransactionCommand.accountTo == createTransactionCommand.accountFrom) {
            CreateTransactionResult.Error.SameAccount.toMono()
        } else {
            accountRepository.findById(createTransactionCommand.accountFrom)
                .flatMap { findResult ->
                    when (findResult) {
                        is AccountRepository.FindAccountResult.Success -> findAccountToAndIfFoundThenCreateTransaction(
                            createTransactionCommand = createTransactionCommand,
                            accountFrom = findResult.account
                        )

                        is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                            CreateTransactionResult.Error.AccountNotFound(createTransactionCommand.accountFrom.value)
                                .toMono()

                        is AccountRepository.FindAccountResult.Error.Unexpected ->
                            CreateTransactionResult.Error.UnexpectedError(findResult.cause).toMono()
                    }
                }
        }

    override fun payCredit(creditPaymentTransactionCommand: CreditPaymentTransactionCommand): Mono<CreditPaymentResult> =
        accountRepository.findById(AccountId(creditPaymentTransactionCommand.accountId))
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> payCredit(
                        account = findResult.account,
                        paymentAmount = creditPaymentTransactionCommand.amount
                    )

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        CreditPaymentResult.Error.AccountNotFound(creditPaymentTransactionCommand.accountId).toMono()

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        CreditPaymentResult.Error.Unexpected(findResult.cause).toMono()
                }
            }

    private fun payCredit(account: Account, paymentAmount: BigDecimal): Mono<CreditPaymentResult> {
        if (isAccountClosedOrBlocked(account)) {
            return CreditPaymentResult.Error.AccountClosedOrBlocked(account.id.value).toMono()
        }

        return Mono.fromCallable {
            val currentAccountBalance = account.balance.value

            if (currentAccountBalance < paymentAmount) {
                // Если средств недостаточно, обновляем баланс до нуля и вычисляем долг
                val updatedAccount = updateAccountBalance(account, BigDecimal.ZERO)
                updatedAccount to (paymentAmount - currentAccountBalance)
            } else {
                // Если средств достаточно, списываем деньги и долг равен нулю
                val updatedAccount = writeOffMoney(account, paymentAmount)
                updatedAccount to BigDecimal.ZERO
            }
        }
            .flatMap { (account, debt) ->
                accountRepository.save(account)
                    .flatMap { saveResult ->
                        when (saveResult) {
                            is AccountRepository.SaveAccountResult.Success -> {
                                kafkaEventSender.sendEventToKafkaAsync(saveResult.account)
                                createCreditTransaction(saveResult.account, paymentAmount, debt)
                            }

                            is AccountRepository.SaveAccountResult.Error -> {
                                CreditPaymentResult.Error.Unexpected(saveResult.cause).toMono()
                            }
                        }
                    }
            }
    }

    private fun createCreditTransaction(
        account: Account,
        amount: BigDecimal,
        debt: BigDecimal
    ): Mono<CreditPaymentResult> {
        val paymentAmount = if (debt != BigDecimal.ZERO)
            amount - debt
        else
            amount

        if (paymentAmount == BigDecimal.ZERO) {
            return CreditPaymentResult.Error.ZeroBalance.toMono()
        }

        val command = CreditPaymentTransactionCommand(
            accountId = account.id.value,
            amount = paymentAmount
        )

        return transactionRepository.save(command)
            .map { saveResult ->
                when (saveResult) {
                    is TransactionRepository.SaveTransactionResult.Success -> {
                        kafkaEventSender.sendEventToKafkaAsync(
                            clientId = account.clientId,
                            transaction = saveResult.transaction
                        )
                        CreditPaymentResult.Success(debt = debt)
                    }

                    is TransactionRepository.SaveTransactionResult.Error -> {
                        CreditPaymentResult.Error.Unexpected(saveResult.cause)
                    }
                }
            }
    }

    private fun findAccountToAndIfFoundThenCreateTransaction(
        createTransactionCommand: CreateTransactionCommand,
        accountFrom: Account
    ): Mono<CreateTransactionResult> =
        accountRepository.findById(createTransactionCommand.accountTo)
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> updateAccountsAndCreateTransaction(
                        MoneyTransfer(
                            accountFrom = accountFrom,
                            accountTo = findResult.account,
                            amount = createTransactionCommand.amount.value
                        )
                    )

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        CreateTransactionResult.Error.AccountNotFound(createTransactionCommand.accountTo.value).toMono()

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        CreateTransactionResult.Error.UnexpectedError(findResult.cause).toMono()
                }
            }

    private fun updateAccountsAndCreateTransaction(
        moneyTransfer: MoneyTransfer
    ): Mono<CreateTransactionResult> {
        if (moneyTransfer.accountFrom.balance.value < moneyTransfer.amount) {
            return CreateTransactionResult.Error.NotEnoughMoney.toMono()
        } else if (isAccountClosedOrBlocked(moneyTransfer.accountFrom) || isAccountClosedOrBlocked(moneyTransfer.accountTo)) {
            return CreateTransactionResult.Error.AccountClosedOrBlocked.toMono()
        }

        val updatedAccountFrom = writeOffMoney(moneyTransfer.accountFrom, moneyTransfer.amount)
        val updatedAccountTo = writeOnMoney(moneyTransfer.accountTo, moneyTransfer.amount)

        return accountRepository.saveAll(listOf(updatedAccountTo, updatedAccountFrom))
            .flatMap { saveAllResult ->
                when (saveAllResult) {
                    is AccountRepository.SaveAllAccountResult.Success -> createTransaction(
                        MoneyTransfer(
                            accountFrom = updatedAccountFrom,
                            accountTo = updatedAccountTo,
                            amount = moneyTransfer.amount
                        )
                    )

                    is AccountRepository.SaveAllAccountResult.Error ->
                        CreateTransactionResult.Error.UnexpectedError(saveAllResult.cause).toMono()
                }
            }
    }

    private fun createTransaction(
        moneyTransfer: MoneyTransfer
    ): Mono<CreateTransactionResult> =
        transactionRepository.save(moneyTransfer)
            .flatMap { saveResult ->
                when (saveResult) {
                    is TransactionRepository.SaveTransactionResult.Success -> {
                        kafkaEventSender.sendEventToKafkaAsync(moneyTransfer.accountFrom)
                        kafkaEventSender.sendEventToKafkaAsync(moneyTransfer.accountTo)
                        processSuccessSaveResult(moneyTransfer.accountFrom.clientId, saveResult.transaction)
                    }

                    is TransactionRepository.SaveTransactionResult.Error ->
                        CreateTransactionResult.Error.SaveErrorFromRepository(saveResult).toMono()
                }
            }
            .onErrorResume { error ->
                log.error("При сохранении произошла неожиданная ошибка", error)
                CreateTransactionResult.Error.UnexpectedError(error).toMono()
            }

    private fun isAccountClosedOrBlocked(account: Account): Boolean =
        account.closedTimestamp != null || account.blockedTimestamp != null

    private fun updateAccountBalance(account: Account, newBalance: BigDecimal): Account =
        account.copy(balance = Balance(newBalance))

    private fun writeOffMoney(account: Account, amount: BigDecimal): Account =
        account.copy(balance = Balance(account.balance.value - amount))

    private fun writeOnMoney(account: Account, amount: BigDecimal): Account =
        account.copy(balance = Balance(account.balance.value + amount))

    private fun processSuccessSaveResult(clientId: ClientId, transaction: Transaction): Mono<CreateTransactionResult> =
        Mono.just(transaction)
            .doOnSuccess { kafkaEventSender.sendEventToKafkaAsync(clientId, it) }
            .map { CreateTransactionResult.Success(transaction) }
}