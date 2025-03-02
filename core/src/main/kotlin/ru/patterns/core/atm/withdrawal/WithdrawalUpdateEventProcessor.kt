package ru.patterns.core.atm.withdrawal

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.patterns.core.atm.EventProcessor
import ru.patterns.core.atm.withdrawal.serialization.Factory
import ru.patterns.core.atm.withdrawal.serialization.WithdrawalRaw
import ru.patterns.core.service.transaction.repository.TransactionRepository

@Component
class WithdrawalUpdateEventProcessor(
    private val transactionRepository: TransactionRepository
) : EventProcessor<WithdrawalRaw> {
    @Transactional
    override fun process(event: WithdrawalRaw): Mono<EventProcessor.ProcessResult> =
        Mono.fromCallable { Factory.Withdrawal(event) }
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