package ru.patterns.core.service.transaction.repository

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.commands.transaction.CreditPaymentTransactionCommand
import ru.patterns.core.domain.MoneyTransfer
import ru.patterns.core.domain.Transaction
import ru.patterns.core.service.transaction.command.serialization.Factory
import ru.patterns.core.service.transaction.command.serialization.Serializer
import ru.patterns.core.service.transaction.entity.TransactionEntity
import ru.patterns.core.service.transaction.repository.TransactionRepository.SaveTransactionResult

sealed interface TransactionRepository {
    fun save(moneyTransfer: MoneyTransfer): Mono<SaveTransactionResult>
    fun save(creditPaymentTransactionCommand: CreditPaymentTransactionCommand): Mono<SaveTransactionResult>

    sealed interface SaveTransactionResult {
        data class Success(val transaction: Transaction) : SaveTransactionResult
        data class Error(val cause: Throwable) : SaveTransactionResult
    }
}

@Component
class TransactionRepositoryImpl(
    private val repository: TransactionR2dbcRepository
) : TransactionRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun save(moneyTransfer: MoneyTransfer): Mono<SaveTransactionResult> =
        Mono.fromCallable { Serializer.TransactionEntity(moneyTransfer) }
            .saveTransaction()

    @Transactional
    override fun save(creditPaymentTransactionCommand: CreditPaymentTransactionCommand): Mono<SaveTransactionResult> =
        Mono.fromCallable { Serializer.TransactionEntity(creditPaymentTransactionCommand) }
            .saveTransaction()


    private fun Mono<TransactionEntity>.saveTransaction() =
        this
            .flatMap { entity -> repository.save(entity) }
            .map(Factory::Transaction)
            .map<SaveTransactionResult>(SaveTransactionResult::Success)
            .doOnError { error -> log.error("При сохранении транзакции произошла ошибка", error) }
            .onErrorResume { error -> SaveTransactionResult.Error(error).toMono() }
}