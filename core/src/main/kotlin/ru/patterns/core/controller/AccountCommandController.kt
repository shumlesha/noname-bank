package ru.patterns.core.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.patterns.core.commands.account.CloseAccountCommand
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.controller.models.ErrorResponse
import ru.patterns.core.controller.models.account.AccountCloseResponse
import ru.patterns.core.controller.models.account.AccountCreateResponse
import ru.patterns.core.service.account.command.AccountCommandService

@RestController
@RequestMapping("/api/account")
class AccountCommandController(
    private val accountCommandService: AccountCommandService
) {
    @PostMapping("/create")
    fun createAccount(@RequestBody createAccountCommand: CreateAccountCommand) =
        accountCommandService.createAccount(createAccountCommand)
            .map { createResult ->
                when (createResult) {
                    is AccountCommandService.CreateAccountResult.Success -> createResult.account.let { account ->
                        AccountCreateResponse(
                            accountId = account.id,
                            accountNumber = account.number,
                            clientId = account.clientId
                        )
                    }

                    is AccountCommandService.CreateAccountResult.Error -> ErrorResponse(
                        message = "При создании счета произошла ошибка",
                        statusCode = 500
                    )
                }
            }

    @PostMapping("/close")
    fun closeAccount(@RequestBody closeAccountCommand: CloseAccountCommand) =
        accountCommandService.closeAccount(closeAccountCommand)
            .map { closeResult ->
                when (closeResult) {
                    is AccountCommandService.CloseAccountResult.Success -> closeResult.account.let { account ->
                        AccountCloseResponse(
                            accountId = account.id,
                            accountNumber = account.number,
                            clientId = account.clientId,
                            closedTimestamp = account.closedTimestamp
                        )
                    }

                    is AccountCommandService.CloseAccountResult.Error.AccountAlreadyClosed -> ErrorResponse(
                        message = "Данный счет уже является закрытым",
                        statusCode = 400
                    )

                    is AccountCommandService.CloseAccountResult.Error.AccountNotExists -> ErrorResponse(
                        message = "Указанного счета не существует",
                        statusCode = 401
                    )

                    else -> ErrorResponse(
                        message = "При закрытии счета произошла ошибка",
                        statusCode = 500
                    )
                }
            }
}