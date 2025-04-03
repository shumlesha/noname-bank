package ru.patterns.core.service.account.repository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.domain.commands.account.CreateAccountCommand
import ru.patterns.core.domain.commands.account.CreateCreditAccountCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.ClientId
import ru.patterns.core.service.account.MasterAccountInitializer.Companion.BANK_ID
import ru.patterns.core.service.account.MasterAccountInitializer.Companion.MASTER_ACCOUNT_NUMBER
import ru.patterns.core.service.account.command.serialization.Factory
import ru.patterns.core.service.account.command.serialization.Serializer
import ru.patterns.core.service.account.entity.AccountEntity
import ru.patterns.core.service.account.repository.AccountRepository.FindAccountResult
import ru.patterns.core.service.account.repository.AccountRepository.FindAllAccountResult
import ru.patterns.core.service.account.repository.AccountRepository.SaveAccountResult
import java.util.UUID

sealed interface AccountRepository {
    fun findById(accountId: AccountId): Mono<FindAccountResult>
    fun findAllByClientId(clientId: ClientId): Mono<FindAllAccountResult>
    fun findMasterAccount(): Mono<FindAccountResult>
    fun saveAll(accounts: List<Account>): Mono<SaveAllAccountResult>
    fun save(createAccountCommand: CreateAccountCommand): Mono<SaveAccountResult>
    fun save(createCreditAccountCommand: CreateCreditAccountCommand): Mono<SaveAccountResult>
    fun save(account: Account): Mono<SaveAccountResult>

    sealed interface FindAccountResult {
        data class Success(val account: Account) : FindAccountResult
        sealed interface Error : FindAccountResult {
            data class AccountNotFound(val accountId: UUID) : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }

    sealed interface FindAllAccountResult {
        data class Success(val accounts: List<Account>) : FindAllAccountResult
        data class Error(val cause: Throwable) : FindAllAccountResult
    }

    sealed interface SaveAllAccountResult {
        data object Success : SaveAllAccountResult
        data class Error(val cause: Throwable) : SaveAllAccountResult
    }

    sealed interface SaveAccountResult {
        data class Success(val account: Account) : SaveAccountResult
        data class Error(val cause: Throwable) : SaveAccountResult
    }
}

@Component
class AccountRepositoryImpl(
    private val repository: AccountR2dbcRepository
) : AccountRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun findById(accountId: AccountId): Mono<FindAccountResult> =
        repository.findById(accountId.value)
            .map(Factory::Account)
            .map<FindAccountResult>(FindAccountResult::Success)
            .onErrorResume { error ->
                log.error("При поиске счета произошла ошибка", error)
                FindAccountResult.Error.Unexpected(error).toMono()
            }
            .switchIfEmpty {
                log.info("Счет с id: {} не найден", accountId)
                FindAccountResult.Error.AccountNotFound(accountId.value).toMono()
            }

    override fun findAllByClientId(clientId: ClientId): Mono<FindAllAccountResult> =
        repository.findAllByClientId(clientId.value)
            .collectList()
            .doOnSuccess { log.info("Получено {} счетов пользователя", it.size) }
            .map { accountEntities -> accountEntities.map(Factory::Account) }
            .map<FindAllAccountResult>(FindAllAccountResult::Success)
            .onErrorResume { error ->
                log.error("При получении счетов клиента: {} произошла ошибка", clientId, error)
                FindAllAccountResult.Error(error).toMono()
            }

    override fun findMasterAccount(): Mono<FindAccountResult> =
        repository.findMasterAccount(BANK_ID, MASTER_ACCOUNT_NUMBER)
            .map(Factory::Account)
            .map<FindAccountResult>(FindAccountResult::Success)
            .onErrorResume { error ->
                log.error("При поиске счета произошла ошибка", error)
                FindAccountResult.Error.Unexpected(error).toMono()
            }
            .switchIfEmpty {
                log.error("Мастер-счет не найден")
                FindAccountResult.Error.Unexpected(IllegalArgumentException("Мастер-счет не найден")).toMono()
            }

    @Transactional
    override fun saveAll(accounts: List<Account>): Mono<AccountRepository.SaveAllAccountResult> =
        Mono.fromCallable { accounts.map(Serializer::AccountEntity) }
            .doOnSuccess { log.info("Сохраняем {} счетов", it.size) }
            .flatMap { entities ->
                repository.saveAll(entities)
                    .then<AccountRepository.SaveAllAccountResult>(AccountRepository.SaveAllAccountResult.Success.toMono())
            }
            .doOnError { error ->
                log.error("При сохранении счета произошла ошибка", error)
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
            }
            .onErrorResume { error -> AccountRepository.SaveAllAccountResult.Error(error).toMono() }

    @Transactional
    override fun save(createAccountCommand: CreateAccountCommand): Mono<SaveAccountResult> =
        Mono.fromCallable { Serializer.AccountEntity(createAccountCommand) }
            .flatMap { accountEntity -> saveEntity(accountEntity) }

    @Transactional
    override fun save(createCreditAccountCommand: CreateCreditAccountCommand): Mono<SaveAccountResult> =
        Mono.fromCallable { Serializer.AccountEntity(createCreditAccountCommand) }
            .flatMap { accountEntity -> saveEntity(accountEntity) }

    @Transactional
    override fun save(account: Account): Mono<SaveAccountResult> =
        Mono.fromCallable { Serializer.AccountEntity(account) }
            .flatMap { accountEntity -> saveEntity(accountEntity) }


    private fun saveEntity(accountEntity: AccountEntity): Mono<SaveAccountResult> =
        repository.save(accountEntity)
            .map(Factory::Account)
            .map<SaveAccountResult>(SaveAccountResult::Success)
            .onErrorResume { error ->
                log.error("При сохранении счета произошла ошибка", error)
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                SaveAccountResult.Error(error).toMono()
            }
}