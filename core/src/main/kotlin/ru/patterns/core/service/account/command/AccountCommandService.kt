package ru.patterns.core.service.account.command

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.commands.account.CloseAccountCommand
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.commands.account.CreateCreditAccountCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.service.account.command.AccountCommandService.CloseAccountResult
import ru.patterns.core.service.account.command.AccountCommandService.CreateAccountResult
import ru.patterns.core.service.account.repository.AccountRepository
import ru.patterns.core.service.kafka.KafkaEventSender
import java.time.LocalDateTime

interface AccountCommandService {
    fun createAccount(createAccountCommand: CreateAccountCommand): Mono<CreateAccountResult>
    fun createCreditAccount(createCreditAccountCommand: CreateCreditAccountCommand): Mono<CreateAccountResult>
    fun closeAccount(closeAccountCommand: CloseAccountCommand): Mono<CloseAccountResult>


    sealed interface CreateAccountResult {
        data class Success(val account: Account) : CreateAccountResult
        data class Error(val cause: Throwable) : CreateAccountResult
    }

    sealed interface CloseAccountResult {
        data class Success(val account: Account) : CloseAccountResult
        sealed interface Error : CloseAccountResult {
            data class FindErrorFromRepository(val error: AccountRepository.FindAccountResult.Error) : Error
            data class SaveErrorFromRepository(val error: AccountRepository.SaveAccountResult.Error) : Error
            data object AccountNotExists : Error
            data class AccountAlreadyClosed(val account: Account) : Error
            data class UnexpectedError(val cause: Throwable) : Error
        }
    }
}

@Service
class AccountCommandServiceImpl(
    private val accountRepository: AccountRepository,
    private val kafkaEventSender: KafkaEventSender
) : AccountCommandService {
    @Transactional
    override fun createAccount(createAccountCommand: CreateAccountCommand): Mono<CreateAccountResult> =
        accountRepository.save(createAccountCommand)
            .flatMap { saveResult ->
                when (saveResult) {
                    is AccountRepository.SaveAccountResult.Success -> processSuccessSaveResult(saveResult)
                    is AccountRepository.SaveAccountResult.Error -> CreateAccountResult.Error(saveResult.cause).toMono()
                }
            }
            .onErrorResume { error -> CreateAccountResult.Error(error).toMono() }

    @Transactional
    override fun createCreditAccount(createCreditAccountCommand: CreateCreditAccountCommand): Mono<CreateAccountResult> =
        accountRepository.save(createCreditAccountCommand)
            .flatMap { saveResult ->
                when (saveResult) {
                    is AccountRepository.SaveAccountResult.Success -> processSuccessSaveResult(saveResult)
                    is AccountRepository.SaveAccountResult.Error -> CreateAccountResult.Error(saveResult.cause).toMono()
                }
            }
            .onErrorResume { error -> CreateAccountResult.Error(error).toMono() }

    @Transactional
    override fun closeAccount(closeAccountCommand: CloseAccountCommand): Mono<CloseAccountResult> =
        accountRepository.findById(closeAccountCommand.accountId)
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> closeAccountIfNeed(findResult.account)

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        CloseAccountResult.Error.FindErrorFromRepository(findResult).toMono()

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        CloseAccountResult.Error.AccountNotExists.toMono()
                }
            }
            .onErrorResume { error -> CloseAccountResult.Error.UnexpectedError(error).toMono() }

    private fun closeAccountIfNeed(account: Account): Mono<CloseAccountResult> =
        Mono.fromCallable { isClosed(account) }
            .flatMap { isClosed ->
                when (isClosed) {
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

    private fun Mono<AccountRepository.SaveAccountResult>.handleSaveResult() =
        this


    private fun isClosed(account: Account): Boolean =
        account.closedTimestamp != null


}