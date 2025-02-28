package ru.patterns.corequery.service.account.query.repository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ru.patterns.corequery.domain.Account
import ru.patterns.corequery.domain.AccountIdentification
import ru.patterns.corequery.domain.ClientId
import ru.patterns.corequery.service.account.query.repository.AccountRepository.FindAccountResult
import ru.patterns.corequery.service.account.query.repository.AccountRepository.FindAllAccountResult
import ru.patterns.corequery.service.account.query.repository.AccountRepository.SaveAccountResult
import ru.patterns.corequery.service.account.query.serialization.Factory
import ru.patterns.corequery.service.account.query.serialization.Serializer

sealed interface AccountRepository {
    fun save(account: Account): Mono<SaveAccountResult>
    fun findAllByClientId(clientId: ClientId): Mono<FindAllAccountResult>
    fun findById(accountIdentification: AccountIdentification): Mono<FindAccountResult>

    sealed interface FindAllAccountResult {
        data class Success(val accounts: List<Account>) : FindAllAccountResult
        data class Error(val cause: Throwable) : FindAllAccountResult
    }

    sealed interface FindAccountResult {
        data class Success(val account: Account) : FindAccountResult
        sealed interface Error : FindAccountResult {
            data object AccountNotFound : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }

    sealed interface SaveAccountResult {
        data object Success : SaveAccountResult
        data class Error(val cause: Throwable) : SaveAccountResult
    }
}

@Component
class AccountRepositoryImpl(
    private val repository: AccountR2dbcRepository
) : AccountRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun save(account: Account): Mono<SaveAccountResult> =
        Mono.fromCallable { Serializer.AccountEntity(account) }
            .flatMap { accountEntity -> repository.save(accountEntity) }
            .map<SaveAccountResult> { SaveAccountResult.Success }
            .doOnError { error -> log.error("При сохранении счета произошла ошибка", error) }
            .onErrorResume { error -> SaveAccountResult.Error(error).toMono() }

    override fun findAllByClientId(clientId: ClientId): Mono<FindAllAccountResult> =
        repository.findAllByClientId(clientId.value)
            .collectList()
            .map { accountEntities -> accountEntities.map(Factory::Account) }
            .map<FindAllAccountResult>(FindAllAccountResult::Success)
            .onErrorResume { error ->
                log.error("При получении счетов клиента: {} произошла ошибка", clientId, error)
                FindAllAccountResult.Error(error).toMono()
            }

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
}