package ru.patterns.core.controller.account

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.controller.account.serialization.CloseAccountCommandRaw
import ru.patterns.core.controller.account.serialization.CreateAccountCommandRaw
import ru.patterns.core.controller.account.serialization.Factory
import ru.patterns.core.controller.account.serialization.Serializer
import ru.patterns.core.idempotent.IdempotencyService
import ru.patterns.core.idempotent.IdempotencyService.Companion.IDEMPOTENCY_KEY
import ru.patterns.core.security.CurrentUser
import ru.patterns.core.service.account.command.AccountCommandService

@RestController
@RequestMapping("/api/account")
class AccountCommandController(
    private val accountCommandService: AccountCommandService,
    private val idempotencyService: IdempotencyService
) {
    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    fun createAccount(
        authentication: Mono<BearerTokenAuthentication>,
        @RequestBody createAccountCommand: CreateAccountCommandRaw,
        @RequestHeader(IDEMPOTENCY_KEY) idempotencyKey: String
    ) =
        idempotencyService.executeOperation(idempotencyKey) {
            authentication
                .map { it.principal }
                .map { principal ->
                    principal as CurrentUser
                    Serializer.CreateAccountCommand(principal, createAccountCommand)
                }
                .flatMap { accountCommandService.createAccount(it) }
                .map { createResult -> Factory.AccountCreateResponse(createResult) }
        } ?: Factory.idempotencyError(idempotencyKey).toMono()

    @PostMapping("/close")
    @PreAuthorize("hasRole('CLIENT')")
    fun closeAccount(
        authentication: Mono<BearerTokenAuthentication>,
        @RequestBody closeAccountCommand: CloseAccountCommandRaw,
        @RequestHeader(IDEMPOTENCY_KEY) idempotencyKey: String
    ) =
        idempotencyService.executeOperation(idempotencyKey) {
            authentication
                .map { it.principal }
                .map { principal ->
                    principal as CurrentUser
                    Serializer.CloseAccountCommand(principal, closeAccountCommand)
                }
                .flatMap { accountCommandService.closeAccount(it) }
                .map { closeResult -> Factory.AccountCloseResponse(closeResult) }
        } ?: Factory.idempotencyError(idempotencyKey).toMono()
}