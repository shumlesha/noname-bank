package ru.patterns.core.atm.deposit

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.patterns.core.atm.AtmService
import ru.patterns.core.atm.EventProcessor
import ru.patterns.core.atm.deposit.serialization.DepositRaw
import ru.patterns.core.atm.deposit.serialization.Factory

@Component
class DepositUpdateEventProcessor(
    private val atmService: AtmService
) : EventProcessor<DepositRaw> {
    @Transactional
    override fun process(event: DepositRaw): Mono<EventProcessor.ProcessResult> =
        Mono.fromCallable { Factory.Deposit(event) }
            .flatMap { deposit -> atmService.deposit(deposit) }
            .map { depositResult ->
                when (depositResult) {
                    is AtmService.DepositResult.Success ->
                        EventProcessor.ProcessResult.Success

                    is AtmService.DepositResult.Error.AccountNotFound ->
                        EventProcessor.ProcessResult.Error(IllegalStateException("Account not found"))

                    is AtmService.DepositResult.Error.AccountClosedOrBlocked ->
                        EventProcessor.ProcessResult.Error(IllegalStateException("Account closed or blocked"))

                    is AtmService.DepositResult.Error.Unexpected ->
                        EventProcessor.ProcessResult.Error(depositResult.cause)
                }
            }
}