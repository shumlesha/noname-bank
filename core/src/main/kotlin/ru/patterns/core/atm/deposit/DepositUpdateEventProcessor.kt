package ru.patterns.core.atm.deposit

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.patterns.core.atm.EventProcessor
import ru.patterns.core.atm.deposit.serialization.DepositRaw
import ru.patterns.core.atm.deposit.serialization.Factory
import ru.patterns.core.service.transaction.repository.TransactionRepository

@Component
class DepositUpdateEventProcessor(
    private val transactionRepository: TransactionRepository
) : EventProcessor<DepositRaw> {
    @Transactional
    override fun process(event: DepositRaw): Mono<EventProcessor.ProcessResult> =
        Mono.fromCallable { Factory.Deposit(event) }
            .flatMap { deposit -> transactionRepository.save(deposit) }
            .map { saveResult ->
                when (saveResult) {
                    is TransactionRepository.SaveTransactionResult.Success ->
                        EventProcessor.ProcessResult.Success

                    is TransactionRepository.SaveTransactionResult.Error ->
                        EventProcessor.ProcessResult.Error(saveResult.cause)
                }
            }
}