package ru.patterns.corequery.service.account.query

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountIdentification
import ru.patterns.core.domain.ClientId
import ru.patterns.corequery.service.account.query.AccountQueryService.FindAllResponse
import ru.patterns.corequery.service.account.query.AccountQueryService.FindByIdResponse
import ru.patterns.corequery.service.account.query.repository.AccountRepository


interface AccountQueryService {
    fun findById(accountIdentification: AccountIdentification): Mono<FindByIdResponse>
    fun findAllByClientId(clientId: ClientId): Mono<FindAllResponse>

    sealed interface FindByIdResponse {
        data class Success(val account: Account) : FindByIdResponse
        sealed interface Error : FindByIdResponse {
            data class ErrorFromRepository(val error: AccountRepository.FindAccountResult.Error) : Error
            data class UnexpectedError(val cause: Throwable) : Error
        }
    }

    sealed interface FindAllResponse {
        data class Success(val accounts: List<Account>) : FindAllResponse
        sealed interface Error : FindAllResponse {
            data class ErrorFromRepository(val error: AccountRepository.FindAllAccountResult.Error) : Error
            data class UnexpectedError(val cause: Throwable) : Error
        }
    }
}

@Component
class AccountQueryServiceImpl(
    private val accountRepository: AccountRepository
) : AccountQueryService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun findById(accountIdentification: AccountIdentification): Mono<FindByIdResponse> =
        accountRepository.findById(accountIdentification)
            .doOnSuccess { log.debug("Получен счет по id: {}", accountIdentification) }
            .doOnError { error ->
                log.error(
                    "При получении счета по id: {} произошла ошибка",
                    accountIdentification,
                    error
                )
            }
            .map { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> FindByIdResponse.Success(findResult.account)

                    is AccountRepository.FindAccountResult.Error -> FindByIdResponse.Error.ErrorFromRepository(
                        findResult
                    )
                }
            }
            .onErrorResume { error -> FindByIdResponse.Error.UnexpectedError(error).toMono() }

    override fun findAllByClientId(clientId: ClientId): Mono<FindAllResponse> =
        accountRepository.findAllByClientId(clientId)
            .doOnSuccess { log.debug("Получены счета клиента с id: {}", clientId) }
            .doOnError { error ->
                log.error(
                    "При получении счетов клиента по id: {} произошла ошибка",
                    clientId,
                    error
                )
            }
            .map { findAllResult ->
                when (findAllResult) {
                    is AccountRepository.FindAllAccountResult.Success ->
                        FindAllResponse.Success(findAllResult.accounts)

                    is AccountRepository.FindAllAccountResult.Error ->
                        FindAllResponse.Error.ErrorFromRepository(findAllResult)
                }
            }
            .onErrorResume { error -> FindAllResponse.Error.UnexpectedError(error).toMono() }
}