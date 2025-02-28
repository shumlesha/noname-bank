package ru.patterns.corequery.listener.transaction

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.patterns.corequery.domain.Transaction
import ru.patterns.corequery.listener.EventProcessor
import ru.patterns.corequery.service.transaction.repository.TransactionRepository

@Component
class TransactionUpdateEventProcessor(
    private val transactionRepository: TransactionRepository
) : EventProcessor<Transaction> {
    @Transactional
    override fun process(event: Transaction): Mono<EventProcessor.ProcessResult> =
        transactionRepository.save(event)
            .map { saveResult ->
                when (saveResult) {
                    is TransactionRepository.SaveTransactionResult.Success ->
                        EventProcessor.ProcessResult.Success

                    is TransactionRepository.SaveTransactionResult.Error ->
                        EventProcessor.ProcessResult.Error(saveResult.cause)
                }
            }
}