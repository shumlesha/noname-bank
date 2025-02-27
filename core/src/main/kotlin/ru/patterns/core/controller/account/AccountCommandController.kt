package ru.patterns.core.controller.account

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.patterns.core.commands.account.CloseAccountCommand
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.controller.account.serialization.Factory
import ru.patterns.core.service.account.command.AccountCommandService

@RestController
@RequestMapping("/api/account")
class AccountCommandController(
    private val accountCommandService: AccountCommandService
) {
    @PostMapping("/create")
    fun createAccount(@RequestBody createAccountCommand: CreateAccountCommand) =
        accountCommandService.createAccount(createAccountCommand)
            .map { createResult -> Factory.AccountCreateResponse(createResult) }

    @PostMapping("/close")
    fun closeAccount(@RequestBody closeAccountCommand: CloseAccountCommand) =
        accountCommandService.closeAccount(closeAccountCommand)
            .map { closeResult -> Factory.AccountCloseResponse(closeResult) }
}