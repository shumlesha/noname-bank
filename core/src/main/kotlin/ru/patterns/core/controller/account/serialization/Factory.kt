package ru.patterns.core.controller.account.serialization

import ru.patterns.core.service.account.command.AccountCommandService

object Factory {
    fun AccountCreateResponse(createResult: AccountCommandService.CreateAccountResult): ApiResponse =
        when (createResult) {
            is AccountCommandService.CreateAccountResult.Success -> createResult.account.let { account ->
                AccountCreateResponse(
                    accountId = account.id.value,
                    accountNumber = account.number.value,
                    clientId = account.clientId.value
                )
            }

            is AccountCommandService.CreateAccountResult.Error -> ErrorResponse(
                message = "При создании счета произошла ошибка",
                statusCode = 500
            )
        }

    fun AccountCloseResponse(closeResult: AccountCommandService.CloseAccountResult): ApiResponse =
        when (closeResult) {
            is AccountCommandService.CloseAccountResult.Success -> closeResult.account.let { account ->
                AccountCloseResponse(
                    accountId = account.id.value,
                    accountNumber = account.number.value,
                    clientId = account.clientId.value,
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