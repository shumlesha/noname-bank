package ru.patterns.core.service.account

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.patterns.core.domain.Account
import ru.patterns.core.models.CreateAccount
import ru.patterns.core.service.account.AccountService.CreateAccountResponse
import ru.patterns.core.service.account.AccountService.FindAccountResponse
import ru.patterns.core.service.account.AccountService.FindByClientResponse
import ru.patterns.core.service.account.command.AccountCommandService
import ru.patterns.core.service.account.query.AccountQueryService
import java.util.UUID

interface AccountService {
    fun findById(accountId: UUID): Mono<FindAccountResponse>

    fun findAllByClientId(clientId: UUID): Mono<FindByClientResponse>

    fun createAccount(createAccount: CreateAccount): Mono<CreateAccountResponse>

    sealed interface FindAccountResponse {
        data class Success(val account: Account) : FindAccountResponse
        data class Error(val cause: Throwable) : FindAccountResponse
    }

    sealed interface FindByClientResponse {
        data class Success(val accounts: List<Account>) : FindByClientResponse
        data class Error(val cause: Throwable) : FindByClientResponse
    }

    sealed interface CreateAccountResponse {
        data object Success : CreateAccountResponse
        data class Error(val cause: Throwable) : CreateAccountResponse
    }
}

@Service
class AccountServiceImpl(
    private val accountQueryService: AccountQueryService,
    private val accountCommandService: AccountCommandService
) : AccountService {
    override fun findById(accountId: UUID): Mono<FindAccountResponse> =
        accountQueryService.findById(accountId)
            .map { response ->
                when (response) {
                    is AccountQueryService.FindByIdResponse.Success -> FindAccountResponse.Success(response.account)
                    is AccountQueryService.FindByIdResponse.Error -> FindAccountResponse.Error(response.cause)
                }
            }

    override fun findAllByClientId(clientId: UUID): Mono<FindByClientResponse> =
        accountQueryService.findAllByClientId(clientId)
            .map { response ->
                when (response) {
                    is AccountQueryService.FindAllResponse.Success -> FindByClientResponse.Success(response.accounts)
                    is AccountQueryService.FindAllResponse.Error -> FindByClientResponse.Error(response.cause)
                }
            }

    override fun createAccount(createAccount: CreateAccount): Mono<CreateAccountResponse> =
        accountCommandService.createAccount(createAccount)
            .map { result ->
                when (result) {
                    is AccountCommandService.CreateAccountResult.Success -> CreateAccountResponse.Success
                    is AccountCommandService.CreateAccountResult.Error -> CreateAccountResponse.Error(result.cause)
            }
    }
}
