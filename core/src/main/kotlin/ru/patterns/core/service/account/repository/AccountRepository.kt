package ru.patterns.core.service.account.repository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountIdentification
import ru.patterns.core.domain.ClientId
import ru.patterns.core.service.account.command.serialization.Factory
import ru.patterns.core.service.account.command.serialization.Serializer
import ru.patterns.core.service.account.entity.AccountEntity
import ru.patterns.core.service.account.repository.AccountRepository.FindAccountResult
import ru.patterns.core.service.account.repository.AccountRepository.FindAllAccountResult
import ru.patterns.core.service.account.repository.AccountRepository.SaveAccountResult

sealed interface AccountRepository {
    fun findById(accountIdentification: AccountIdentification): Mono<FindAccountResult>
    fun findAllByClientId(clientId: ClientId): Mono<FindAllAccountResult>
    fun saveAll(accounts: List<Account>): Mono<SaveAllAccountResult>
    fun save(createAccountCommand: CreateAccountCommand): Mono<SaveAccountResult>
    fun save(account: Account): Mono<SaveAccountResult>

    sealed interface FindAccountResult {
        data class Success(val account: Account) : FindAccountResult
        sealed interface Error : FindAccountResult {
            data object AccountNotFound : Error
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

    override fun findById(accountIdentification: AccountIdentification): Mono<FindAccountResult> =
        repository.findByClientIdAndId(
            clientId = accountIdentification.clientId.value,
            accountId = accountIdentification.accountId.value
        )
            .map(Factory::Account)
            .map<FindAccountResult>(FindAccountResult::Success)
            .onErrorResume { error ->
                log.error("При поиске счета произошла ошибка", error)
                FindAccountResult.Error.Unexpected(error).toMono()
            }
            .switchIfEmpty {
                log.info("Счет с id: {} не найден", accountIdentification)
                FindAccountResult.Error.AccountNotFound.toMono()
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

    override fun saveAll(accounts: List<Account>): Mono<AccountRepository.SaveAllAccountResult> =
        Mono.fromCallable { accounts.map(Serializer::AccountEntity) }
            .doOnSuccess { log.info("Сохраняем {} счетов", it.size) }
            .flatMap { entities ->
                repository.saveAll(entities)
                    .then<AccountRepository.SaveAllAccountResult>(AccountRepository.SaveAllAccountResult.Success.toMono())
            }
            .doOnError { error -> log.error("При сохранении счета произошла ошибка", error) }
            .onErrorResume { error -> AccountRepository.SaveAllAccountResult.Error(error).toMono() }

    override fun save(createAccountCommand: CreateAccountCommand): Mono<SaveAccountResult> =
        Mono.fromCallable { Serializer.AccountEntity(createAccountCommand) }
            .flatMap { accountEntity -> saveEntity(accountEntity) }

    override fun save(account: Account): Mono<SaveAccountResult> =
        Mono.fromCallable { Serializer.AccountEntity(account) }
            .flatMap { accountEntity -> saveEntity(accountEntity) }


    private fun saveEntity(accountEntity: AccountEntity): Mono<SaveAccountResult> =
        repository.save(accountEntity)
            .map(Factory::Account)
            .map<SaveAccountResult>(SaveAccountResult::Success)
            .onErrorResume { error ->
                log.error("При сохранении счета произошла ошибка", error)
                SaveAccountResult.Error(error).toMono()
            }
}