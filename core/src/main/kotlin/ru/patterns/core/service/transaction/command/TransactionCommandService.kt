package ru.patterns.core.service.transaction.command

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.commands.transaction.CreateTransactionCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.MoneyTransfer
import ru.patterns.core.domain.Transaction
import ru.patterns.core.service.account.repository.AccountRepository
import ru.patterns.core.service.kafka.KafkaEventSender
import ru.patterns.core.service.transaction.command.TransactionCommandService.CreateTransactionResult
import ru.patterns.core.service.transaction.repository.TransactionRepository
import java.math.BigDecimal
import java.util.UUID

sealed interface TransactionCommandService {
    fun create(createTransactionCommand: CreateTransactionCommand): Mono<CreateTransactionResult>

    sealed interface CreateTransactionResult {
        data class Success(val transaction: Transaction) : CreateTransactionResult
        sealed interface Error : CreateTransactionResult {
            data class SaveErrorFromRepository(val error: TransactionRepository.SaveTransactionResult.Error) : Error
            data object NotEnoughMoney : Error
            data object SameAccount : Error
            data class AccountNotFound(val accountId: UUID) : Error
            data class UnexpectedError(val cause: Throwable) : Error
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

    override fun create(createTransactionCommand: CreateTransactionCommand): Mono<CreateTransactionResult> {
        if (createTransactionCommand.accountTo == createTransactionCommand.accountFrom) {
            return CreateTransactionResult.Error.SameAccount.toMono()
        }

        return accountRepository.findById(AccountId(createTransactionCommand.accountFrom))
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> findAccountToAndIfFoundThenCreateTransaction(
                        createTransactionCommand = createTransactionCommand,
                        accountFrom = findResult.account
                    )

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        CreateTransactionResult.Error.AccountNotFound(createTransactionCommand.accountFrom).toMono()

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        CreateTransactionResult.Error.UnexpectedError(findResult.cause).toMono()
                }
            }
    }

    private fun findAccountToAndIfFoundThenCreateTransaction(
        createTransactionCommand: CreateTransactionCommand,
        accountFrom: Account
    ): Mono<CreateTransactionResult> =
        accountRepository.findById(AccountId(createTransactionCommand.accountTo))
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> updateAccountsAndCreateTransaction(
                        MoneyTransfer(
                            accountFrom = accountFrom,
                            accountTo = findResult.account,
                            amount = createTransactionCommand.amount
                        )
                    )

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        CreateTransactionResult.Error.AccountNotFound(createTransactionCommand.accountTo).toMono()

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        CreateTransactionResult.Error.UnexpectedError(findResult.cause).toMono()
                }
            }

    private fun updateAccountsAndCreateTransaction(
        moneyTransfer: MoneyTransfer
    ): Mono<CreateTransactionResult> {
        if (moneyTransfer.accountFrom.balance.value < moneyTransfer.amount) {
            return CreateTransactionResult.Error.NotEnoughMoney.toMono()
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
                    is TransactionRepository.SaveTransactionResult.Success ->
                        processSuccessSaveResult(saveResult.transaction)

                    is TransactionRepository.SaveTransactionResult.Error ->
                        CreateTransactionResult.Error.SaveErrorFromRepository(saveResult).toMono()
                }
            }
            .onErrorResume { error ->
                log.error("При сохранении произошла неожиданная ошибка", error)
                CreateTransactionResult.Error.UnexpectedError(error).toMono()
            }

    private fun writeOffMoney(accountFrom: Account, amount: BigDecimal): Account =
        accountFrom.copy(balance = Balance(accountFrom.balance.value - amount))

    private fun writeOnMoney(accountTo: Account, amount: BigDecimal): Account =
        accountTo.copy(balance = Balance(accountTo.balance.value + amount))

    private fun processSuccessSaveResult(transaction: Transaction): Mono<CreateTransactionResult> =
        Mono.just(transaction)
            .doOnSuccess { kafkaEventSender.sendEventToKafkaAsync(it) }
            .map { CreateTransactionResult.Success(transaction) }
}