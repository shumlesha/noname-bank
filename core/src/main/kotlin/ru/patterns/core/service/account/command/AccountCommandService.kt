package ru.patterns.core.service.account.command

import reactor.core.publisher.Mono
import ru.patterns.core.models.CreateAccount

interface AccountCommandService {
    fun createAccount(createAccount: CreateAccount): Mono<CreateAccountResult>

    sealed interface CreateAccountResult {
        data object Success : CreateAccountResult
        data class Error(val cause: Throwable) : CreateAccountResult
    }
}