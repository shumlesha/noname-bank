package ru.patterns.corequery.controller.transaction.serialization

import ru.patterns.corequery.domain.Transaction
import ru.patterns.corequery.service.transaction.TransactionQueryService

object Serializer {
    fun FindAllResponse(findAllResponse: TransactionQueryService.FindAllResponse): TransactionResponse =
        when (findAllResponse) {
            is TransactionQueryService.FindAllResponse.Success -> FindAllTransactionResponse(
                findAllResponse.transactions.map { transaction ->
                    FindTransactionResponse(transaction)
                }
            )

            is TransactionQueryService.FindAllResponse.Error ->
                ErrorResponse(
                    message = "При получении транзакций произошла ошибка",
                    statusCode = 500
                )
        }

    fun FindResponse(findResponse: TransactionQueryService.FindByIdResponse): TransactionResponse =
        when (findResponse) {
            is TransactionQueryService.FindByIdResponse.Success ->
                FindTransactionResponse(findResponse.transaction)

            is TransactionQueryService.FindByIdResponse.Error.NotFound ->
                ErrorResponse(
                    message = "Указанная транзакция не найдена",
                    statusCode = 401
                )

            is TransactionQueryService.FindByIdResponse.Error ->
                ErrorResponse(
                    message = "При получении транзакций произошла ошибка",
                    statusCode = 500
                )
        }

    private fun FindTransactionResponse(transaction: Transaction): FindTransactionResponse =
        FindTransactionResponse(
            id = transaction.id.value,
            transactionTimestamp = transaction.transactionTimestamp,
            accountFrom = transaction.accountFrom?.value,
            accountTo = transaction.accountTo.value,
            amount = transaction.amount.value,
            clientId = transaction.clientId.value
        )
}