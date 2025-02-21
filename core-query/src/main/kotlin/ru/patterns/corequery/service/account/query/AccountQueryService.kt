package ru.patterns.corequery.service.account.query

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.corequery.domain.Account
import ru.patterns.corequery.domain.AccountId
import ru.patterns.corequery.domain.ClientId
import ru.patterns.corequery.service.account.query.AccountQueryService.FindAllResponse
import ru.patterns.corequery.service.account.query.AccountQueryService.FindByIdResponse
import ru.patterns.corequery.service.account.query.repository.AccountRepository
import ru.patterns.corequery.service.account.query.serialization.Factory


interface AccountQueryService {
    fun findById(accountId: AccountId): Mono<FindByIdResponse>
    fun findAllByClientId(clientId: ClientId): Mono<FindAllResponse>

    sealed interface FindByIdResponse {
        data class Success(val account: Account) : FindByIdResponse
        data class Error(val cause: Throwable) : FindByIdResponse
    }

    sealed interface FindAllResponse {
        data class Success(val accounts: List<Account>) : FindAllResponse
        data class Error(val cause: Throwable) : FindAllResponse
    }
}

@Component
class AccountQueryServiceImpl(
    private val accountRepository: AccountRepository
) : AccountQueryService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun findById(accountId: AccountId): Mono<FindByIdResponse> =
        accountRepository.findById(accountId.value)
            .doOnSuccess { log.debug("Получен счет по id: {}", accountId) }
            .doOnError { error ->
                log.error(
                    "При получении счета по id: {} произошла ошибка",
                    accountId,
                    error
                )
            }
            .map(Factory::Account)
            .map<FindByIdResponse>(FindByIdResponse::Success)
            .onErrorResume { FindByIdResponse.Error(it).toMono() }

    override fun findAllByClientId(clientId: ClientId): Mono<FindAllResponse> =
        accountRepository.findAllByClientId(clientId.value)
            .doOnSuccess { log.debug("Получены счета клиента с id: {}", clientId) }
            .doOnError { error ->
                log.error(
                    "При получении счетов клиента по id: {} произошла ошибка",
                    clientId,
                    error
                )
            }
            .map { accounts -> accounts.map(Factory::Account) }
            .map<FindAllResponse>(FindAllResponse::Success)
            .onErrorResume { FindAllResponse.Error(it).toMono() }
}