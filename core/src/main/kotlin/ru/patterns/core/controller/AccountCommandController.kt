package ru.patterns.core.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.patterns.core.commands.account.CloseAccountCommand
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.service.account.command.AccountCommandService

@RestController
@RequestMapping("/api/account")
class AccountCommandController(
    private val accountCommandService: AccountCommandService
) {
    @PostMapping("/test")
    fun test(@RequestBody createAccountCommand: CreateAccountCommand) =
        accountCommandService.createAccount(createAccountCommand)

    @PostMapping("/close")
    fun close(@RequestBody closeAccountCommand: CloseAccountCommand) =
        accountCommandService.closeAccount(closeAccountCommand)
}