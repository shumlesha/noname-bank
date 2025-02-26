package ru.patterns.corequery.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.patterns.core.domain.AccountIdentification
import ru.patterns.core.domain.ClientIdentification
import ru.patterns.corequery.controller.models.ErrorResponse
import ru.patterns.corequery.controller.models.Factory
import ru.patterns.corequery.service.account.query.AccountQueryService

@RestController
@RequestMapping("/api/account/query")
class AccountController(
    private val accountQueryService: AccountQueryService
) {
    @PostMapping
    fun getAccount(@RequestBody accountIdentification: AccountIdentification) =
        accountQueryService.findById(accountIdentification)
            .map { findResult ->
                when (findResult) {
                    is AccountQueryService.FindByIdResponse.Success ->
                        findResult.account.let { account -> Factory.FindAccountResponse(account) }

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
            }

    @PostMapping("/list")
    fun getClientAccounts(@RequestBody clientIdentification: ClientIdentification) =
        accountQueryService.findAllByClientId(clientIdentification.clientId)
            .map { findAllResult ->
                when (findAllResult) {
                    is AccountQueryService.FindAllResponse.Success ->
                        findAllResult.accounts.let { accounts -> Factory.FindAllAccountsResponse(accounts) }

                    is AccountQueryService.FindAllResponse.Error ->
                        ErrorResponse(
                            message = "При получении счетов произошла ошибка",
                            statusCode = 500
                        )
                }
            }
}