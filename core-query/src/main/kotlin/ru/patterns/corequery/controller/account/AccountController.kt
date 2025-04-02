package ru.patterns.corequery.controller.account

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.patterns.core.security.CurrentUser
import ru.patterns.corequery.controller.account.serialization.AccountIdentificationRaw
import ru.patterns.corequery.controller.account.serialization.Factory
import ru.patterns.corequery.controller.account.serialization.GetAccountsWithPaginationRaw
import ru.patterns.corequery.controller.account.serialization.Serializer
import ru.patterns.corequery.domain.AccountId
import ru.patterns.corequery.domain.AccountIdentification
import ru.patterns.corequery.domain.ClientId
import ru.patterns.corequery.domain.ClientIdentification
import ru.patterns.corequery.service.account.query.AccountQueryService

@RestController
@RequestMapping("/api/query/account")
class AccountController(
    private val accountQueryService: AccountQueryService
) {
    @PostMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    fun getMyAccount(
        authentication: Mono<BearerTokenAuthentication>,
        @RequestBody accountIdentification: AccountIdentificationRaw
    ) =
        authentication
            .map { it.principal }
            .map { principal ->
                principal as CurrentUser
                AccountIdentification(
                    clientId = ClientId(principal.id),
                    accountId = AccountId(accountIdentification.accountId)
                )
            }
            .flatMap { accountQueryService.findById(it) }
            .map { findResult -> Serializer.FindResponse(findResult) }

    @PostMapping("/list/my")
    @PreAuthorize("hasRole('CLIENT')")
    fun getMyAccounts(authentication: Mono<BearerTokenAuthentication>) =
        authentication
            .map { it.principal }
            .map { principal ->
                principal as CurrentUser

                ClientId(principal.id)
            }
            .flatMap { accountQueryService.findAllByClientId(it) }
            .map { findAllResult -> Serializer.FindAllResponse(findAllResult) }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getAccount(@RequestBody accountIdentification: AccountIdentification) =
        accountQueryService.findById(accountIdentification)
            .map { findResult -> Serializer.FindResponse(findResult) }

    @PostMapping("/list")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getClientAccounts(@RequestBody clientIdentification: ClientIdentification) =
        accountQueryService.findAllByClientId(clientIdentification.clientId)
            .map { findAllResult -> Serializer.FindAllResponse(findAllResult) }

    @PostMapping("/all")
    @PreAuthorize("hasRole('EMPLOYEE')")
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