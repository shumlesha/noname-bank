package ru.patterns.core.blocking

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.blocking.ClientBlockingEventProcessor.ClientAccountsBlockResult
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.ClientId
import ru.patterns.core.service.account.repository.AccountRepository
import java.time.LocalDateTime

sealed interface ClientBlockingEventProcessor {
    fun blockClientAccounts(clientId: ClientId): Mono<ClientAccountsBlockResult>

    sealed interface ClientAccountsBlockResult {
        data object Success : ClientAccountsBlockResult
        sealed interface Error : ClientAccountsBlockResult {
            data class SaveAllErrorFromRepository(
                val error: AccountRepository.SaveAllAccountResult.Error
            ) : ClientAccountsBlockResult

            data class FindAllErrorFromRepository(
                val error: AccountRepository.FindAllAccountResult.Error
            ) : ClientAccountsBlockResult

            data class Unexpected(val error: Throwable) : ClientAccountsBlockResult
        }
    }
}

@Component
class AccountBlockingEventProcessor(
    private val accountRepository: AccountRepository
) : ClientBlockingEventProcessor {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun blockClientAccounts(clientId: ClientId): Mono<ClientAccountsBlockResult> =
        accountRepository.findAllByClientId(clientId)
            .flatMap { findAllResult ->
                when (findAllResult) {
                    is AccountRepository.FindAllAccountResult.Success ->
                        blockClientAccounts(findAllResult.accounts)

                    is AccountRepository.FindAllAccountResult.Error ->
                        ClientAccountsBlockResult.Error.FindAllErrorFromRepository(findAllResult).toMono()
                }
            }
            .onErrorResume { error ->
                log.error("При блокировке счетов произошла ошибка", error)
                ClientAccountsBlockResult.Error.Unexpected(error).toMono()
            }

    private fun blockClientAccounts(accounts: List<Account>): Mono<ClientAccountsBlockResult> =
        Flux.fromIterable(accounts)
            .map { account -> account.copy(blockedTimestamp = LocalDateTime.now()) }
            .collectList()
            .flatMap { blockedAccounts -> accountRepository.saveAll(blockedAccounts) }
            .map { saveAllResult ->
                when (saveAllResult) {
                    is AccountRepository.SaveAllAccountResult.Success ->
                        ClientAccountsBlockResult.Success

                    is AccountRepository.SaveAllAccountResult.Error ->
                        ClientAccountsBlockResult.Error.SaveAllErrorFromRepository(saveAllResult)
                }
            }

}