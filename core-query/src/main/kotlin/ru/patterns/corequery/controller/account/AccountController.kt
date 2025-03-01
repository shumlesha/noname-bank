package ru.patterns.corequery.controller.account

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.patterns.corequery.controller.account.serialization.Factory
import ru.patterns.corequery.controller.account.serialization.GetAccountsWithPaginationRaw
import ru.patterns.corequery.controller.account.serialization.Serializer
import ru.patterns.corequery.domain.AccountIdentification
import ru.patterns.corequery.domain.ClientIdentification
import ru.patterns.corequery.service.account.query.AccountQueryService

@RestController
@RequestMapping("/api/query/account")
class AccountController(
    private val accountQueryService: AccountQueryService
) {
    @PostMapping
    fun getAccount(@RequestBody accountIdentification: AccountIdentification) =
        accountQueryService.findById(accountIdentification)
            .map { findResult -> Serializer.FindResponse(findResult) }

    @PostMapping("/list")
    fun getClientAccounts(@RequestBody clientIdentification: ClientIdentification) =
        accountQueryService.findAllByClientId(clientIdentification.clientId)
            .map { findAllResult -> Serializer.FindAllResponse(findAllResult) }

    @PostMapping("/all")
    fun getAllAccounts(@RequestBody getAccountsWithPaginationRaw: GetAccountsWithPaginationRaw) =
        Mono.fromCallable { Factory.GetAccountsWithPagination(getAccountsWithPaginationRaw) }
            .flatMap { accountQueryService.findAllWithPagination(it) }
            .map { findAllWithPagination ->
                Serializer.FindAllWithPaginationResponse(
                    findAllWithPaginationResult = findAllWithPagination,
                    page = getAccountsWithPaginationRaw.offset
                )
            }
}