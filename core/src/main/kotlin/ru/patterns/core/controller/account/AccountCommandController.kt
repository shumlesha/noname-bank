package ru.patterns.core.controller.account

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.patterns.core.controller.account.serialization.CloseAccountCommandRaw
import ru.patterns.core.controller.account.serialization.CreateAccountCommandRaw
import ru.patterns.core.controller.account.serialization.Factory
import ru.patterns.core.controller.account.serialization.Serializer
import ru.patterns.core.security.CurrentUser
import ru.patterns.core.service.account.command.AccountCommandService

@RestController
@RequestMapping("/api/account")
class AccountCommandController(
    private val accountCommandService: AccountCommandService
) {
    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    fun createAccount(
        authentication: Mono<BearerTokenAuthentication>,
        @RequestBody createAccountCommand: CreateAccountCommandRaw
    ) =
        authentication
            .map { it.principal }
            .map { principal ->
                principal as CurrentUser
                Serializer.CreateAccountCommand(principal, createAccountCommand)
            }
            .flatMap { accountCommandService.createAccount(it) }
            .map { createResult -> Factory.AccountCreateResponse(createResult) }

    @PostMapping("/close")
    @PreAuthorize("hasRole('CLIENT')")
    fun closeAccount(
        authentication: Mono<BearerTokenAuthentication>,
        @RequestBody closeAccountCommand: CloseAccountCommandRaw
    ) =
        authentication
            .map { it.principal }
            .map { principal ->
                principal as CurrentUser
                Serializer.CloseAccountCommand(principal, closeAccountCommand)
            }
            .flatMap { accountCommandService.closeAccount(it) }
            .map { closeResult -> Factory.AccountCloseResponse(closeResult) }
}