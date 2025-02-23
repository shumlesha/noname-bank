package ru.patterns.corequery.listener.account

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import ru.patterns.core.domain.Account
import ru.patterns.corequery.listener.EventProcessor
import ru.patterns.corequery.service.account.query.repository.AccountRepository

@Component
class AccountUpdateEventProcessor(
    private val accountRepository: AccountRepository
) : EventProcessor<Account> {
    @Transactional
    override fun process(event: Account): Mono<EventProcessor.ProcessResult> =
        accountRepository.save(event)
            .map { saveResult ->
                when (saveResult) {
                    is AccountRepository.SaveAccountResult.Success -> EventProcessor.ProcessResult.Success
                    is AccountRepository.SaveAccountResult.Error -> EventProcessor.ProcessResult.Error(saveResult.cause)
                }
            }
}