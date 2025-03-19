package ru.patterns.core.service.transaction.command

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.commands.transaction.CreateTransactionCommand
import ru.patterns.core.commands.transaction.CreditPaymentTransactionCommand
import ru.patterns.core.config.CurrencyProperties
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.ClientId
import ru.patterns.core.domain.CurrencyCode
import ru.patterns.core.domain.MoneyTransfer
import ru.patterns.core.domain.Transaction
import ru.patterns.core.service.account.repository.AccountRepository
import ru.patterns.core.service.currency.CurrencyService
import ru.patterns.core.service.currency.serialization.ConvertCurrencyRequest
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
            data object ZeroAmountTransaction : Error
            data object ConvertationUnavailable : Error
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
    private val currencyService: CurrencyService,
    private val accountRepository: AccountRepository,
    private val kafkaEventSender: KafkaEventSender,
    currencyProperties: CurrencyProperties
) : TransactionCommandService {
    private val log = LoggerFactory.getLogger(this::class.java)!!
    private val taxCoefficient = BigDecimal.valueOf(currencyProperties.taxCoefficient)

    private companion object {
        val BIG_DECIMAL_HUNDRED: BigDecimal = BigDecimal.valueOf(100)
    }

    @Transactional
    override fun create(createTransactionCommand: CreateTransactionCommand): Mono<CreateTransactionResult> =
        if (createTransactionCommand.accountTo == createTransactionCommand.accountFrom) {
            CreateTransactionResult.Error.SameAccount.toMono()
        } else if (createTransactionCommand.amount.value == BigDecimal.ZERO) {
            CreateTransactionResult.Error.ZeroAmountTransaction.toMono()
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

    @Transactional
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
                                findMasterAndCreateCreditTransaction(saveResult.account, paymentAmount, debt)
                                    .doOnSuccess { result ->
                                        if (result is CreditPaymentResult.Success) {
                                            kafkaEventSender.sendEventToKafkaAsync(saveResult.account)
                                        }
                                    }
                            }

                            is AccountRepository.SaveAccountResult.Error -> {
                                CreditPaymentResult.Error.Unexpected(saveResult.cause).toMono()
                            }
                        }
                    }
            }
    }

    private fun findMasterAndCreateCreditTransaction(
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

        return accountRepository.findMasterAccount()
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> {
                        val updatedMaster = writeOnMoney(findResult.account, paymentAmount)

                        accountRepository.save(updatedMaster)
                            .flatMap { saveResult ->
                                when (saveResult) {
                                    is AccountRepository.SaveAccountResult.Success -> {
                                        val command = CreditPaymentTransactionCommand(
                                            accountId = account.id.value,
                                            amount = paymentAmount
                                        )

                                        createCreditPaymentTransaction(
                                            command = command,
                                            clientId = account.clientId,
                                            debt = debt
                                        )
                                            .doOnSuccess { result ->
                                                if (result is CreditPaymentResult.Success) {
                                                    kafkaEventSender.sendEventToKafkaAsync(saveResult.account)
                                                }
                                            }
                                    }

                                    is AccountRepository.SaveAccountResult.Error -> {
                                        CreditPaymentResult.Error.Unexpected(saveResult.cause).toMono()
                                    }
                                }
                            }
                    }

                    is AccountRepository.FindAccountResult.Error.Unexpected -> {
                        CreditPaymentResult.Error.Unexpected(findResult.cause).toMono()
                    }

                    else -> {
                        log.error("При оплате кредита произошла неизвестная ошибка")
                        CreditPaymentResult.Error.Unexpected(IllegalArgumentException("Произошла неизвестная ошибка"))
                            .toMono()
                    }
                }
            }

    }

    private fun createCreditPaymentTransaction(
        command: CreditPaymentTransactionCommand,
        clientId: ClientId,
        debt: BigDecimal
    ): Mono<CreditPaymentResult> =
        transactionRepository.save(command)
            .map { saveResult ->
                when (saveResult) {
                    is TransactionRepository.SaveTransactionResult.Success -> {
                        kafkaEventSender.sendEventToKafkaAsync(
                            clientId = clientId,
                            transaction = saveResult.transaction
                        )
                        CreditPaymentResult.Success(debt = debt)
                    }

                    is TransactionRepository.SaveTransactionResult.Error -> {
                        CreditPaymentResult.Error.Unexpected(saveResult.cause)
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

        return Mono.fromCallable { isAccountCurrencyTypeDiffer(moneyTransfer) }
            .flatMap { differResult ->
                if (differResult) {
                    val tax = getTaxAmount(moneyTransfer.amount)
                    val convertRequest = ConvertCurrencyRequest(
                        currencyFrom = CurrencyCode(moneyTransfer.accountFrom.currency),
                        currencyTo = CurrencyCode(moneyTransfer.accountTo.currency),
                        amount = moneyTransfer.amount - tax
                    )

                    currencyService.convertCurrency(convertRequest)
                        .flatMap { convertResult ->
                            when (convertResult) {
                                is CurrencyService.ConvertCurrencyResult.Success -> {
                                    val updatedAccountFrom =
                                        writeOffMoney(moneyTransfer.accountFrom, moneyTransfer.amount)
                                    val updatedAccountTo =
                                        writeOnMoney(moneyTransfer.accountTo, convertResult.response.convertedAmount)

                                    accrueTaxToMasterAsync(tax)

                                    saveUpdatedAccountAndCreateTransaction(
                                        updatedAccountFrom = updatedAccountFrom,
                                        updatedAccountTo = updatedAccountTo,
                                        moneyTransfer = moneyTransfer
                                    )
                                }

                                is CurrencyService.ConvertCurrencyResult.Error.BankUnavailable -> {
                                    CreateTransactionResult.Error.ConvertationUnavailable.toMono()
                                }

                                is CurrencyService.ConvertCurrencyResult.Error.Unexpected -> {
                                    CreateTransactionResult.Error.UnexpectedError(convertResult.cause).toMono()
                                }
                            }
                        }
                } else {
                    val updatedAccountFrom = writeOffMoney(moneyTransfer.accountFrom, moneyTransfer.amount)
                    val updatedAccountTo = writeOnMoney(moneyTransfer.accountTo, moneyTransfer.amount)

                    saveUpdatedAccountAndCreateTransaction(
                        updatedAccountFrom = updatedAccountFrom,
                        updatedAccountTo = updatedAccountTo,
                        moneyTransfer = moneyTransfer
                    )
                }
            }

    }

    private fun saveUpdatedAccountAndCreateTransaction(
        updatedAccountFrom: Account,
        updatedAccountTo: Account,
        moneyTransfer: MoneyTransfer
    ): Mono<CreateTransactionResult> =
        accountRepository.saveAll(listOf(updatedAccountTo, updatedAccountFrom))
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

    private fun accrueTaxToMasterAsync(tax: BigDecimal) {
        accountRepository.findMasterAccount()
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> {
                        val updatedMasterAccount = writeOnMoney(findResult.account, tax)

                        accountRepository.save(updatedMasterAccount)
                            .doOnSuccess { result ->
                                if (result is AccountRepository.SaveAccountResult.Success) {
                                    kafkaEventSender.sendEventToKafkaAsync(updatedMasterAccount)
                                }
                            }
                    }

                    is AccountRepository.FindAccountResult.Error -> {
                        log.error("Комиссия потеряна :(")
                        Unit.toMono()
                    }
                }
            }
            .subscribe()
    }

    private fun isAccountCurrencyTypeDiffer(moneyTransfer: MoneyTransfer) =
        moneyTransfer.accountFrom.currency != moneyTransfer.accountTo.currency

    private fun createTransaction(
        moneyTransfer: MoneyTransfer
    ): Mono<CreateTransactionResult> =
        transactionRepository.save(moneyTransfer)
            .flatMap { saveResult ->
                when (saveResult) {
                    is TransactionRepository.SaveTransactionResult.Success -> {
                        processSuccessSaveResult(moneyTransfer.accountFrom.clientId, saveResult.transaction)
                            .doOnSuccess {
                                kafkaEventSender.sendEventToKafkaAsync(moneyTransfer.accountFrom)
                                kafkaEventSender.sendEventToKafkaAsync(moneyTransfer.accountTo)
                            }
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

    private fun getTaxAmount(amount: BigDecimal) =
        amount * taxCoefficient / BIG_DECIMAL_HUNDRED
}