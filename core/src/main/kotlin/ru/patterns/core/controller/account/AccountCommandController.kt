package ru.patterns.core.controller.account

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.controller.account.serialization.CloseAccountCommandRaw
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
        @AuthenticationPrincipal currentUser: CurrentUser,
        @RequestBody createAccountCommand: CreateAccountCommand
    ) =
        accountCommandService.createAccount(createAccountCommand)
            .map { createResult -> Factory.AccountCreateResponse(createResult) }

    @PostMapping("/close")
    @PreAuthorize("hasRole('CLIENT')")
    fun closeAccount(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @RequestBody closeAccountCommand: CloseAccountCommandRaw
    ) =
        Mono.fromCallable { Serializer.CloseAccountCommand(currentUser, closeAccountCommand) }
            .flatMap { accountCommandService.closeAccount(it) }
            .map { closeResult -> Factory.AccountCloseResponse(closeResult) }
}