package ru.patterns.corequery.service.account.query

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.corequery.domain.Account
import ru.patterns.corequery.domain.AccountIdentification
import ru.patterns.corequery.domain.ClientId
import ru.patterns.corequery.domain.GetAccountsWithPagination
import ru.patterns.corequery.service.account.query.AccountQueryService.FindAllResponse
import ru.patterns.corequery.service.account.query.AccountQueryService.FindAllWithPaginationResponse
import ru.patterns.corequery.service.account.query.AccountQueryService.FindByIdResponse
import ru.patterns.corequery.service.account.query.repository.AccountRepository


interface AccountQueryService {
    fun findById(accountIdentification: AccountIdentification): Mono<FindByIdResponse>
    fun findAllByClientId(clientId: ClientId): Mono<FindAllResponse>
    fun findAllWithPagination(getAccountsWithPagination: GetAccountsWithPagination): Mono<FindAllWithPaginationResponse>

    sealed interface FindByIdResponse {
        data class Success(val account: Account) : FindByIdResponse
        sealed interface Error : FindByIdResponse {
            data object NotFound : Error
            data object ClientIsNotOwner : Error
            data class UnexpectedError(val cause: Throwable) : Error
        }
    }

    sealed interface FindAllResponse {
        data class Success(val accounts: List<Account>) : FindAllResponse
        data class Error(val cause: Throwable) : FindAllResponse
    }

    sealed interface FindAllWithPaginationResponse {
        data class Success(val accounts: List<Account>) : FindAllWithPaginationResponse
        data class Error(val cause: Throwable) : FindAllWithPaginationResponse
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
                    is AccountRepository.FindAccountResult.Success ->
                        FindByIdResponse.Success(findResult.account)

                    is AccountRepository.FindAccountResult.Error.Unexpected ->
                        FindByIdResponse.Error.UnexpectedError(findResult.cause)

                    is AccountRepository.FindAccountResult.Error.AccountNotFound ->
                        FindByIdResponse.Error.NotFound
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
                        FindAllResponse.Error(findAllResult.cause)
                }
            }
            .onErrorResume { error -> FindAllResponse.Error(error).toMono() }

    override fun findAllWithPagination(getAccountsWithPagination: GetAccountsWithPagination): Mono<FindAllWithPaginationResponse> =
        accountRepository.findAllWithPagination(getAccountsWithPagination)
            .doOnSuccess { log.debug("Получены счета: {}", it) }
            .map { findAllWithPaginationResult ->
                when (findAllWithPaginationResult) {
                    is AccountRepository.FindAllWithPaginationResult.Success ->
                        FindAllWithPaginationResponse.Success(findAllWithPaginationResult.accounts)

                    is AccountRepository.FindAllWithPaginationResult.Error ->
                        FindAllWithPaginationResponse.Error(findAllWithPaginationResult.cause)
                }
            }
            .onErrorResume { error -> FindAllWithPaginationResponse.Error(error).toMono() }
}