package ru.patterns.core.service.kafka.listener.transaction

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.patterns.core.controller.transaction.serialization.CreateTransactionCommandRaw
import ru.patterns.core.controller.transaction.serialization.Factory
import ru.patterns.core.controller.transaction.serialization.Serializer
import ru.patterns.core.service.kafka.listener.EventProcessor
import ru.patterns.core.service.transaction.command.TransactionCommandService

@Component
class TransactionUpdateEventProcessor(
    private val transactionCommandService: TransactionCommandService
) : EventProcessor<CreateTransactionCommandRaw> {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun process(event: CreateTransactionCommandRaw): Mono<EventProcessor.ProcessResult> =
        Mono.fromCallable { Factory.CreateCreditTransactionCommand(event) }
            .flatMap { command -> transactionCommandService.create(command) }
            .doOnSuccess { log.info(Serializer.CreateTransactionResponse(it).toString()) }
            .map { result ->
                when (result) {
                    is TransactionCommandService.CreateTransactionResult.Success ->
                        EventProcessor.ProcessResult.Success

                    is TransactionCommandService.CreateTransactionResult.Error.UnexpectedError ->
                        EventProcessor.ProcessResult.Error(result.cause)

                    is TransactionCommandService.CreateTransactionResult.Error ->
                        EventProcessor.ProcessResult.Error()
                }
            }
}