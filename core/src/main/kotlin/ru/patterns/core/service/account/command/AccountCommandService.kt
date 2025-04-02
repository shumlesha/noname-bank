package ru.patterns.core.service.account.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.commands.account.CloseAccountCommand
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.commands.account.CreateCreditAccountCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.ClientId
import ru.patterns.core.service.account.MasterAccountInitializer.Companion.BANK_ID
import ru.patterns.core.service.account.MasterAccountInitializer.Companion.MASTER_ACCOUNT_NUMBER
import ru.patterns.core.service.account.command.AccountCommandService.CloseAccountResult
import ru.patterns.core.service.account.command.AccountCommandService.CreateAccountResult
import ru.patterns.core.service.account.command.AccountCommandService.CreateCreditAccountResult
import ru.patterns.core.service.account.repository.AccountRepository
import ru.patterns.core.service.currency.CurrencyService
import ru.patterns.core.service.kafka.KafkaEventSender
import java.time.LocalDateTime

interface AccountCommandService {
    fun createAccount(createAccountCommand: CreateAccountCommand): Mono<CreateAccountResult>
    fun createCreditAccount(createCreditAccountCommand: CreateCreditAccountCommand): Mono<CreateCreditAccountResult>
    fun closeAccount(closeAccountCommand: CloseAccountCommand): Mono<CloseAccountResult>

    sealed interface CreateAccountResult {
        data class Success(val account: Account) : CreateAccountResult
        sealed interface Error : CreateAccountResult {
            data object NonExistentCurrency : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }

    sealed interface CreateCreditAccountResult {
        data class Success(val account: Account) : CreateCreditAccountResult
        sealed interface Error : CreateCreditAccountResult {
            data object BankDontHaveSuchMoney : Error
            data class Unexpected(val cause: Throwable? = null) : Error
        }
    }

    sealed interface CloseAccountResult {
        data class Success(val account: Account) : CloseAccountResult
        sealed interface Error : CloseAccountResult {
            data class FindErrorFromRepository(val error: AccountRepository.FindAccountResult.Error) : Error
            data class SaveErrorFromRepository(val error: AccountRepository.SaveAccountResult.Error) : Error
            data object AccountNotExists : Error
            data object MasterAccountCantBeClosed : Error
            data class AccountAlreadyClosed(val account: Account) : Error
            data object ClientIsNotOwner : Error
            data class AccountBlocked(val account: Account) : Error
            data class UnexpectedError(val cause: Throwable) : Error
        }
    }
}

@Service
class AccountCommandServiceImpl(
    private val accountRepository: AccountRepository,
    private val currencyService: CurrencyService,
    private val masterAccountService: MasterAccountService,
    private val kafkaEventSender: KafkaEventSender
) : AccountCommandService {
    @Transactional
    override fun createAccount(createAccountCommand: CreateAccountCommand): Mono<CreateAccountResult> =
        currencyService.getCurrencyRate(createAccountCommand.currency.value)
            .flatMap { getCurrencyResult ->
                when (getCurrencyResult) {
                    is CurrencyService.GetCurrencyRateResult.Success ->
                        accountRepository.save(createAccountCommand)
                            .flatMap { saveResult ->
                                when (saveResult) {
                                    is AccountRepository.SaveAccountResult.Success ->
                                        processSuccessSaveResult(saveResult)

                                    is AccountRepository.SaveAccountResult.Error ->
                                        CreateAccountResult.Error.Unexpected(saveResult.cause).toMono()
                                }
                            }

                    is CurrencyService.GetCurrencyRateResult.Error.NonExistentCurrency ->
                        CreateAccountResult.Error.NonExistentCurrency.toMono()

                    is CurrencyService.GetCurrencyRateResult.Error.Unexpected ->
                        CreateAccountResult.Error.Unexpected(getCurrencyResult.cause).toMono()

                    CurrencyService.GetCurrencyRateResult.Error.BankUnavailable ->
                        CreateAccountResult.Error.Unexpected(IllegalArgumentException("Банк недоступен")).toMono()
                }
            }
            .doOnError { TransactionAspectSupport.currentTransactionStatus().setRollbackOnly() }
            .onErrorResume { error -> CreateAccountResult.Error.Unexpected(error).toMono() }

    @Transactional
    override fun createCreditAccount(createCreditAccountCommand: CreateCreditAccountCommand): Mono<CreateCreditAccountResult> =
        masterAccountService.giveCredit(createCreditAccountCommand)
            .map { giveCreditResult ->
                when (giveCreditResult) {
                    is MasterAccountService.GiveCreditResponse.Success ->
                        CreateCreditAccountResult.Success(giveCreditResult.creditAccount)

                    is MasterAccountService.GiveCreditResponse.Error.NotEnoughMoney ->
                        CreateCreditAccountResult.Error.BankDontHaveSuchMoney

                    is MasterAccountService.GiveCreditResponse.Error ->
                        CreateCreditAccountResult.Error.Unexpected()
                }
            }
            .doOnError { TransactionAspectSupport.currentTransactionStatus().setRollbackOnly() }
            .onErrorResume { error -> CreateCreditAccountResult.Error.Unexpected(error).toMono() }

    @Transactional
    override fun closeAccount(closeAccountCommand: CloseAccountCommand): Mono<CloseAccountResult> =
        accountRepository.findById(closeAccountCommand.accountId)
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> closeAccountIfNeed(
                        closeAccountCommand.clientId,
                        findResult.account
                    )

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        CloseAccountResult.Error.FindErrorFromRepository(findResult).toMono()

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        CloseAccountResult.Error.AccountNotExists.toMono()
                }
            }
            .doOnError { TransactionAspectSupport.currentTransactionStatus().setRollbackOnly() }
            .onErrorResume { error -> CloseAccountResult.Error.UnexpectedError(error).toMono() }

    private fun closeAccountIfNeed(clientId: ClientId, account: Account): Mono<CloseAccountResult> {
        if (isMasterAccount(account)) {
            return CloseAccountResult.Error.MasterAccountCantBeClosed.toMono()
        } else if (!isClientOwner(clientId, account))
            return CloseAccountResult.Error.ClientIsNotOwner.toMono()
        else if (isBlocked(account))
            return CloseAccountResult.Error.AccountBlocked(account).toMono()

        return when (isClosed(account)) {
            true -> CloseAccountResult.Error.AccountAlreadyClosed(account).toMono()
            false -> closeAccount(account)
        }
    }

    private fun closeAccount(account: Account): Mono<CloseAccountResult> =
        Mono.fromCallable { createAccountWithClosedTimestamp(account) }
            .flatMap { accountWithClosedTimestamp -> accountRepository.save(accountWithClosedTimestamp) }
            .flatMap { saveResult ->
                when (saveResult) {
                    is AccountRepository.SaveAccountResult.Success ->
                        processCloseSuccessSaveResult(saveResult)

                    is AccountRepository.SaveAccountResult.Error ->
                        CloseAccountResult.Error.SaveErrorFromRepository(saveResult).toMono()
                }
            }
            .onErrorResume { error -> CloseAccountResult.Error.UnexpectedError(error).toMono() }

    private fun createAccountWithClosedTimestamp(account: Account): Account =
        account.copy(closedTimestamp = LocalDateTime.now())

    private fun processSuccessSaveResult(saveResult: AccountRepository.SaveAccountResult.Success): Mono<CreateAccountResult> =
        Mono.just(saveResult.account)
            .doOnSuccess { account -> kafkaEventSender.sendEventToKafkaAsync(account) }
            .map(CreateAccountResult::Success)

    private fun processCloseSuccessSaveResult(saveResult: AccountRepository.SaveAccountResult.Success): Mono<CloseAccountResult> =
        Mono.just(saveResult.account)
            .doOnSuccess { account -> kafkaEventSender.sendEventToKafkaAsync(account) }
            .map(CloseAccountResult::Success)

    private fun isClientOwner(clientId: ClientId, account: Account) =
        account.clientId == clientId

    private fun isBlocked(account: Account): Boolean =
        account.blockedTimestamp != null

    private fun isClosed(account: Account): Boolean =
        account.closedTimestamp != null

    private fun isMasterAccount(account: Account) =
        account.clientId.value == BANK_ID && account.number.value == MASTER_ACCOUNT_NUMBER
}