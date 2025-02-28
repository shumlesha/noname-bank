package ru.patterns.corequery.controller.account.serialization

import ru.patterns.corequery.domain.Account
import ru.patterns.corequery.service.account.query.AccountQueryService

object Serializer {
    fun FindResponse(findResult: AccountQueryService.FindByIdResponse): AccountResponse =
        when (findResult) {
            is AccountQueryService.FindByIdResponse.Success ->
                FindAccountResponse(findResult.account)

            is AccountQueryService.FindByIdResponse.Error.NotFound ->
                ErrorResponse(
                    message = "Указанный счет не найден",
                    statusCode = 401
                )

            is AccountQueryService.FindByIdResponse.Error.UnexpectedError ->
                ErrorResponse(
                    message = "При получении счета произошла ошибка",
                    statusCode = 500
                )
        }

    fun FindAllResponse(findAllResult: AccountQueryService.FindAllResponse): AccountResponse =
        when (findAllResult) {
            is AccountQueryService.FindAllResponse.Success ->
                FindAllAccountsResponse(findAllResult.accounts)

            is AccountQueryService.FindAllResponse.Error ->
                ErrorResponse(
                    message = "При получении счетов произошла ошибка",
                    statusCode = 500
                )
        }

    private fun FindAccountResponse(account: Account) =
        FindAccountResponse(
            id = account.id.value,
            blockedTimestamp = account.blockedTimestamp,
            closedTimestamp = account.closedTimestamp,
            clientId = account.clientId.value,
            number = account.number.value,
            balance = account.balance.value,
            isCredit = account.isCredit
        )

    private fun FindAllAccountsResponse(accounts: List<Account>) =
        FindAllAccountsResponse(
            data = accounts.map(Serializer::FindAccountResponse)
        )
}