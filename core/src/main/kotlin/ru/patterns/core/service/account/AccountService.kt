package ru.patterns.core.service.account

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.service.account.AccountService.CreateAccountResponse
import ru.patterns.core.service.account.command.AccountCommandService

interface AccountService {
    fun createAccount(createAccountCommand: CreateAccountCommand): Mono<CreateAccountResponse>

    sealed interface CreateAccountResponse {
        data object Success : CreateAccountResponse
        data class Error(val cause: Throwable) : CreateAccountResponse
    }
}

@Service
class AccountServiceImpl(
    private val accountCommandService: AccountCommandService
) : AccountService {
    override fun createAccount(createAccountCommand: CreateAccountCommand): Mono<CreateAccountResponse> =
        accountCommandService.createAccount(createAccountCommand)
            .map { result ->
                when (result) {
                    is AccountCommandService.CreateAccountResult.Success -> CreateAccountResponse.Success
                    is AccountCommandService.CreateAccountResult.Error -> CreateAccountResponse.Error(result.cause)
                }
            }
}
