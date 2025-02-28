package ru.patterns.corequery.service.transaction

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.corequery.domain.ClientId
import ru.patterns.corequery.domain.Transaction
import ru.patterns.corequery.domain.TransactionInfo
import ru.patterns.corequery.service.transaction.TransactionQueryService.FindAllResponse
import ru.patterns.corequery.service.transaction.TransactionQueryService.FindByIdResponse
import ru.patterns.corequery.service.transaction.repository.TransactionRepository

sealed interface TransactionQueryService {
    fun findById(transactionInfo: TransactionInfo): Mono<FindByIdResponse>
    fun findAllByClientId(clientId: ClientId): Mono<FindAllResponse>

    sealed interface FindByIdResponse {
        data class Success(val transaction: Transaction) : FindByIdResponse
        sealed interface Error : FindByIdResponse {
            data object NotFound : Error
            data class UnexpectedError(val cause: Throwable) : Error
        }
    }

    sealed interface FindAllResponse {
        data class Success(val transactions: List<Transaction>) : FindAllResponse
        data class Error(val cause: Throwable) : FindAllResponse
    }
}

@Component
class TransactionQueryServiceImpl(
    private val transactionRepository: TransactionRepository
) : TransactionQueryService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun findById(transactionInfo: TransactionInfo): Mono<FindByIdResponse> =
        transactionRepository.findById(transactionInfo)
            .doOnSuccess { log.debug("Получена транзакция по id: {}", transactionInfo) }
            .doOnError { error ->
                log.error(
                    "При получении транзакции по id: {} произошла ошибка",
                    transactionInfo,
                    error
                )
            }
            .map { findResult ->
                when (findResult) {
                    is TransactionRepository.FindTransactionResult.Success ->
                        FindByIdResponse.Success(findResult.transaction)

                    is TransactionRepository.FindTransactionResult.Error.Unexpected ->
                        FindByIdResponse.Error.UnexpectedError(findResult.cause)

                    is TransactionRepository.FindTransactionResult.Error.AccountNotFound ->
                        FindByIdResponse.Error.NotFound
                }
            }
            .onErrorResume { error -> FindByIdResponse.Error.UnexpectedError(error).toMono() }

    override fun findAllByClientId(clientId: ClientId): Mono<FindAllResponse> =
        transactionRepository.findAllByClientId(clientId)
            .doOnSuccess { log.debug("Получены транзакции клиента с id: {}", clientId) }
            .doOnError { error ->
                log.error(
                    "При получении счетов клиента по id: {} произошла ошибка",
                    clientId,
                    error
                )
            }
            .map { findAllResult ->
                when (findAllResult) {
                    is TransactionRepository.FindAllTransactionResult.Success ->
                        FindAllResponse.Success(findAllResult.transactions)

                    is TransactionRepository.FindAllTransactionResult.Error ->
                        FindAllResponse.Error(findAllResult.cause)
                }
            }
            .onErrorResume { error -> FindAllResponse.Error(error).toMono() }
}
