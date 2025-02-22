package ru.patterns.core.service.account.repository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.domain.AccountIdentification
import ru.patterns.core.domain.ClientId
import ru.patterns.core.service.account.entity.AccountEntity
import ru.patterns.core.service.account.repository.AccountRepository.FindAccountResult
import ru.patterns.core.service.account.repository.AccountRepository.FindAllAccountResult

sealed interface AccountRepository {
    fun findAllClientAccounts(clientId: ClientId): Mono<FindAllAccountResult>
    fun findAccount(accountIdentification: AccountIdentification): Mono<FindAccountResult>

    sealed interface FindAllAccountResult {
        data class Success(val accounts: List<AccountEntity>) : FindAllAccountResult
        data class Error(val cause: Throwable) : FindAllAccountResult
    }

    sealed interface FindAccountResult {
        data class Success(val account: AccountEntity) : FindAccountResult
        sealed interface Error : FindAccountResult {
            data object AccountNotFound : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }
}

@Component
class AccountRepositoryImpl(
    private val repository: AccountR2dbcRepository
) : AccountRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun findAllClientAccounts(clientId: ClientId): Mono<FindAllAccountResult> =
        TODO()

    override fun findAccount(accountIdentification: AccountIdentification): Mono<FindAccountResult> =
        repository.findByClientIdAndId(
            clientId = accountIdentification.clientId.value,
            accountId = accountIdentification.accountId.value
        )
            .map { accountEntity ->
                accountEntity?.let {
                    FindAccountResult.Success(accountEntity)
                } ?: FindAccountResult.Error.AccountNotFound
            }
            .onErrorResume { error ->
                log.error("При поиске счета произошла ошибка", error)
                FindAccountResult.Error.Unexpected(error).toMono()
            }
}