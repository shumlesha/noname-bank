package ru.patterns.corequery.controller.transaction

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.security.CurrentUser
import ru.patterns.corequery.controller.account.serialization.AccountIdentificationRaw
import ru.patterns.corequery.controller.transaction.serialization.ErrorResponse
import ru.patterns.corequery.controller.transaction.serialization.Serializer
import ru.patterns.corequery.controller.transaction.serialization.TransactionResponse
import ru.patterns.corequery.domain.AccountId
import ru.patterns.corequery.domain.AccountIdentification
import ru.patterns.corequery.domain.ClientId
import ru.patterns.corequery.domain.ClientIdentification
import ru.patterns.corequery.domain.TransactionInfo
import ru.patterns.corequery.service.transaction.TransactionQueryService

@RestController
@RequestMapping("/api/query/transaction")
class TransactionController(
    private val transactionQueryService: TransactionQueryService
) {
    @PostMapping("/client")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getAllUserTransactions(@RequestBody clientIdentification: ClientIdentification): Mono<TransactionResponse> =
        transactionQueryService.findAllByClientId(clientIdentification.clientId)
            .map { findAllResult -> Serializer.FindAllResponse(findAllResult) }

    @PostMapping("/client/my")
    @PreAuthorize("hasRole('CLIENT')")
    fun getAllMyTransactions(authentication: Mono<BearerTokenAuthentication>): Mono<TransactionResponse> =
        authentication
            .map { it.principal }
            .map { principal ->
                principal as CurrentUser
                ClientId(principal.id)
            }
            .flatMap { clientId -> transactionQueryService.findAllByClientId(clientId) }
            .map { findAllResult -> Serializer.FindAllResponse(findAllResult) }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getTransactionInfo(@RequestBody transactionInfo: TransactionInfo): Mono<TransactionResponse> =
        transactionQueryService.findById(transactionInfo)
            .map { findAllResult -> Serializer.FindResponse(findAllResult) }

    @PostMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    fun getMyTransactionInfo(
        authentication: Mono<BearerTokenAuthentication>,
        @RequestBody transactionInfo: TransactionInfo
    ): Mono<TransactionResponse> =
        authentication
            .map { it.principal }
            .flatMap { principal ->
                principal as CurrentUser

                if (transactionInfo.clientId.value == principal.id) {
                    transactionQueryService.findById(transactionInfo)
                        .map { findAllResult -> Serializer.FindResponse(findAllResult) }
                } else {
                    ErrorResponse(
                        message = "Транзакция не принадлежит пользователю",
                        statusCode = 403
                    ).toMono()
                }
            }


    @PostMapping("/account/my")
    @PreAuthorize("hasRole('CLIENT')")
    fun getAllMyAccountTransactions(
        authentication: Mono<BearerTokenAuthentication>,
        @RequestBody accountIdentification: AccountIdentificationRaw
    ): Mono<TransactionResponse> =
        authentication
            .map { it.principal }
            .map { principal ->
                principal as CurrentUser

                AccountIdentification(
                    clientId = ClientId(principal.id),
                    accountId = AccountId(accountIdentification.accountId)
                )
            }
            .flatMap { transactionQueryService.findAllByAccountId(it) }
            .map { findAllResult -> Serializer.FindAllResponse(findAllResult) }

    @PostMapping("/account")
    @PreAuthorize("hasRole('EMPLOYEE')")
    fun getAllAccountTransactions(@RequestBody accountIdentification: AccountIdentification): Mono<TransactionResponse> =
        transactionQueryService.findAllByAccountId(accountIdentification)
            .map { findAllResult -> Serializer.FindAllResponse(findAllResult) }
}