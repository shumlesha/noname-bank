package ru.patterns.core.atm

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.atm.AtmService.DepositResult
import ru.patterns.core.atm.AtmService.WithdrawalResult
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.Balance
import ru.patterns.core.domain.Deposit
import ru.patterns.core.domain.Transaction
import ru.patterns.core.domain.Withdrawal
import ru.patterns.core.service.account.repository.AccountRepository
import ru.patterns.core.service.kafka.KafkaEventSender
import ru.patterns.core.service.transaction.repository.TransactionRepository

sealed interface AtmService {
    fun deposit(deposit: Deposit): Mono<DepositResult>
    fun withdrawal(withdrawal: Withdrawal): Mono<WithdrawalResult>

    sealed interface DepositResult {
        data class Success(val transaction: Transaction) : DepositResult
        sealed interface Error : DepositResult {
            data object AccountClosedOrBlocked : Error
            data object AccountNotFound : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }

    sealed interface WithdrawalResult {
        data class Success(val transaction: Transaction) : WithdrawalResult
        sealed interface Error : WithdrawalResult {
            data object AccountClosedOrBlocked : Error
            data object AccountNotFound : Error
            data object NotEnoughMoney : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }
}

@Component
class AtmServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val kafkaEventSender: KafkaEventSender
) : AtmService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun deposit(deposit: Deposit): Mono<DepositResult> =
        accountRepository.findById(deposit.accountId)
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success ->
                        updateAccountBalanceAndCreateTransaction(findResult.account, deposit)

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        DepositResult.Error.Unexpected(findResult.cause).toMono()

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        DepositResult.Error.AccountNotFound.toMono()
                }
            }

    override fun withdrawal(withdrawal: Withdrawal): Mono<WithdrawalResult> =
        accountRepository.findById(withdrawal.accountId)
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success ->
                        updateAccountBalanceAndCreateTransaction(findResult.account, withdrawal)

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        WithdrawalResult.Error.Unexpected(findResult.cause).toMono()

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        WithdrawalResult.Error.AccountNotFound.toMono()
                }
            }

    private fun updateAccountBalanceAndCreateTransaction(
        account: Account,
        withdrawal: Withdrawal
    ): Mono<WithdrawalResult> {
        if (isClosedOrBlocked(account)) {
            return WithdrawalResult.Error.AccountClosedOrBlocked.toMono()
        } else if (account.balance.value < withdrawal.amount.value) {
            return WithdrawalResult.Error.NotEnoughMoney.toMono()
        }

        val updatedAccount = writeOffMoney(account, withdrawal.amount)

        return accountRepository.save(updatedAccount)
            .flatMap { saveResult ->
                when (saveResult) {
                    is AccountRepository.SaveAccountResult.Success ->
                        createTransaction(withdrawal)
                            .sendWithdrawalEventsToKafkaAsync(saveResult.account)

                    is AccountRepository.SaveAccountResult.Error ->
                        WithdrawalResult.Error.Unexpected(saveResult.cause).toMono()
                }
            }
    }

    private fun updateAccountBalanceAndCreateTransaction(account: Account, deposit: Deposit): Mono<DepositResult> {
        if (isClosedOrBlocked(account)) {
            return DepositResult.Error.AccountClosedOrBlocked.toMono()
        }

        val updatedAccount = writeOnMoney(account, deposit.amount)

        return accountRepository.save(updatedAccount)
            .flatMap { saveResult ->
                when (saveResult) {
                    is AccountRepository.SaveAccountResult.Success ->
                        createTransaction(deposit)
                            .sendDepositEventsToKafkaAsync(saveResult.account)

                    is AccountRepository.SaveAccountResult.Error ->
                        DepositResult.Error.Unexpected(saveResult.cause).toMono()
                }
            }
    }

    private fun createTransaction(deposit: Deposit): Mono<DepositResult> =
        transactionRepository.save(deposit)
            .map { saveResult ->
                when (saveResult) {
                    is TransactionRepository.SaveTransactionResult.Success -> DepositResult.Success(saveResult.transaction)
                    is TransactionRepository.SaveTransactionResult.Error -> DepositResult.Error.Unexpected(saveResult.cause)
                }
            }

    private fun createTransaction(withdrawal: Withdrawal): Mono<WithdrawalResult> =
        transactionRepository.save(withdrawal)
            .map { saveResult ->
                when (saveResult) {
                    is TransactionRepository.SaveTransactionResult.Success -> WithdrawalResult.Success(saveResult.transaction)
                    is TransactionRepository.SaveTransactionResult.Error -> WithdrawalResult.Error.Unexpected(saveResult.cause)
                }
            }


    private fun Mono<DepositResult>.sendDepositEventsToKafkaAsync(account: Account) =
        this
            .doOnSuccess { result ->
                when (result) {
                    is DepositResult.Success -> {
                        kafkaEventSender.sendEventToKafkaAsync(account)
                        kafkaEventSender.sendEventToKafkaAsync(account.clientId, result.transaction)
                        log.debug("События о депозите денег на счет отправлены в query сервис")
                    }

                    is DepositResult.Error -> {
                        log.debug("События о депозите денег на счет не были отправлены в query сервис")
                    }
                }
            }

    private fun Mono<WithdrawalResult>.sendWithdrawalEventsToKafkaAsync(account: Account) =
        this
            .doOnSuccess { result ->
                when (result) {
                    is WithdrawalResult.Success -> {
                        kafkaEventSender.sendEventToKafkaAsync(account)
                        kafkaEventSender.sendEventToKafkaAsync(account.clientId, result.transaction)
                        log.debug("События о снятии денег со счета отправлены в query сервис")
                    }

                    is WithdrawalResult.Error -> {
                        log.debug("События о снятии денег со счета не были отправлены в query сервис")
                    }
                }
            }

    private fun isClosedOrBlocked(account: Account): Boolean =
        account.closedTimestamp != null || account.blockedTimestamp != null

    private fun writeOffMoney(account: Account, amount: Balance): Account =
        account.copy(balance = Balance(account.balance.value - amount.value))

    private fun writeOnMoney(account: Account, amount: Balance): Account =
        account.copy(balance = Balance(account.balance.value + amount.value))
}