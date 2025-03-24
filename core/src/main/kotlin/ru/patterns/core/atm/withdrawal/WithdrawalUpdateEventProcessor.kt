package ru.patterns.core.atm.withdrawal

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.patterns.core.atm.AtmService
import ru.patterns.core.atm.withdrawal.serialization.Factory
import ru.patterns.core.atm.withdrawal.serialization.WithdrawalRaw
import ru.patterns.core.service.kafka.listener.EventProcessor

@Component
class WithdrawalUpdateEventProcessor(
    private val atmService: AtmService
) : EventProcessor<WithdrawalRaw> {
    @Transactional
    override fun process(event: WithdrawalRaw): Mono<EventProcessor.ProcessResult> =
        Mono.fromCallable { Factory.Withdrawal(event) }
            .flatMap { withdrawal -> atmService.withdrawal(withdrawal) }
            .map { withdrawalResult ->
                when (withdrawalResult) {
                    is AtmService.WithdrawalResult.Success ->
                        EventProcessor.ProcessResult.Success

                    is AtmService.WithdrawalResult.Error.AccountNotFound ->
                        EventProcessor.ProcessResult.Error(IllegalStateException("Account not found"))

                    is AtmService.WithdrawalResult.Error.NotEnoughMoney ->
                        EventProcessor.ProcessResult.Error(IllegalStateException("Not enough money on account"))

                    is AtmService.WithdrawalResult.Error.AccountClosedOrBlocked ->
                        EventProcessor.ProcessResult.Error(IllegalStateException("Account closed or blocked"))

                    is AtmService.WithdrawalResult.Error.Unexpected ->
                        EventProcessor.ProcessResult.Error(withdrawalResult.cause)
                }
            }
}