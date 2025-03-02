package ru.patterns.core.controller.transaction.serialization

import ru.patterns.core.service.transaction.command.TransactionCommandService

object Factory {
    fun CreateTransactionResponse(createResult: TransactionCommandService.CreateTransactionResult): ApiResponse =
        when (createResult) {
            is TransactionCommandService.CreateTransactionResult.Success -> createResult.transaction.let { transaction ->
                CreateTransactionResponse(
                    transactionId = transaction.id,
                    accountTo = transaction.accountTo!!,
                    accountFrom = transaction.accountFrom!!,
                    amount = transaction.amount.value
                )
            }

            is TransactionCommandService.CreateTransactionResult.Error.UnexpectedError ->
                ErrorResponse(
                    message = "При создании транзакции произошла неожиданная ошибка",
                    statusCode = 500
                )

            is TransactionCommandService.CreateTransactionResult.Error.AccountNotFound ->
                ErrorResponse(
                    message = "Счет с id ${createResult.accountId} не найден",
                    statusCode = 404
                )

            is TransactionCommandService.CreateTransactionResult.Error.NotEnoughMoney ->
                ErrorResponse(
                    message = "На счете недостаточно средств",
                    statusCode = 400
                )

            is TransactionCommandService.CreateTransactionResult.Error.SameAccount ->
                ErrorResponse(
                    message = "Нельзя перевести деньги на тот же самый счет, с которого их снимают",
                    statusCode = 400
                )

            is TransactionCommandService.CreateTransactionResult.Error.AccountClosedOrBlocked ->
                ErrorResponse(
                    message = "Счет закрыт или заблокирован",
                    statusCode = 400
                )

            else -> ErrorResponse(
                message = "Что-то пошло не так",
                statusCode = 500
            )
        }
}