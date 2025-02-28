package ru.patterns.corequery.service.transaction.repository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import ru.patterns.corequery.domain.ClientId
import ru.patterns.corequery.domain.Transaction
import ru.patterns.corequery.domain.TransactionInfo
import ru.patterns.corequery.service.transaction.repository.TransactionRepository.FindAllTransactionResult
import ru.patterns.corequery.service.transaction.repository.TransactionRepository.FindTransactionResult
import ru.patterns.corequery.service.transaction.repository.TransactionRepository.SaveTransactionResult
import ru.patterns.corequery.service.transaction.serialization.Factory
import ru.patterns.corequery.service.transaction.serialization.Serializer

sealed interface TransactionRepository {
    fun save(transaction: Transaction): Mono<SaveTransactionResult>
    fun findAllByClientId(clientId: ClientId): Mono<FindAllTransactionResult>
    fun findById(transactionInfo: TransactionInfo): Mono<FindTransactionResult>

    sealed interface FindAllTransactionResult {
        data class Success(val transactions: List<Transaction>) : FindAllTransactionResult
        data class Error(val cause: Throwable) : FindAllTransactionResult
    }

    sealed interface FindTransactionResult {
        data class Success(val transaction: Transaction) : FindTransactionResult
        sealed interface Error : FindTransactionResult {
            data object AccountNotFound : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }

    sealed interface SaveTransactionResult {
        data object Success : SaveTransactionResult
        data class Error(val cause: Throwable) : SaveTransactionResult
    }
}

@Component
class TransactionRepositoryImpl(
    private val repository: TransactionR2dbcRepository
) : TransactionRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun save(transaction: Transaction): Mono<SaveTransactionResult> =
        Mono.fromCallable { Serializer.TransactionEntity(transaction) }
            .flatMap { transactionEntity -> repository.save(transactionEntity) }
            .map<SaveTransactionResult> { SaveTransactionResult.Success }
            .doOnError { error -> log.error("При сохранении счета произошла ошибка", error) }
            .onErrorResume { error -> SaveTransactionResult.Error(error).toMono() }

    override fun findAllByClientId(clientId: ClientId): Mono<FindAllTransactionResult> =
        repository.findAllByClientId(clientId.value)
            .collectList()
            .map { transactionEntities -> transactionEntities.map(Factory::Transaction) }
            .map<FindAllTransactionResult>(FindAllTransactionResult::Success)
            .onErrorResume { error ->
                log.error("При получении счетов клиента: {} произошла ошибка", clientId, error)
                FindAllTransactionResult.Error(error).toMono()
            }

    override fun findById(transactionInfo: TransactionInfo): Mono<FindTransactionResult> =
        repository.findByIdAndClientId(
            clientId = transactionInfo.clientId.value,
            transactionId = transactionInfo.transactionId.value
        )
            .map(Factory::Transaction)
            .map<FindTransactionResult>(FindTransactionResult::Success)
            .onErrorResume { error ->
                log.error("При поиске транзакции произошла ошибка", error)
                FindTransactionResult.Error.Unexpected(error).toMono()
            }
            .switchIfEmpty {
                log.info("Транзакция с id: {} не найдена", transactionInfo)
                FindTransactionResult.Error.AccountNotFound.toMono()
            }

}