package ru.patterns.corequery.controller.transaction

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.patterns.corequery.controller.transaction.serialization.Serializer
import ru.patterns.corequery.controller.transaction.serialization.TransactionResponse
import ru.patterns.corequery.domain.ClientIdentification
import ru.patterns.corequery.domain.TransactionInfo
import ru.patterns.corequery.service.transaction.TransactionQueryService

@RestController
@RequestMapping("/api/query/transaction")
class TransactionController(
    private val transactionQueryService: TransactionQueryService
) {
    @PostMapping("/list")
    fun getAllUserTransactions(@RequestBody clientIdentification: ClientIdentification): Mono<TransactionResponse> =
        transactionQueryService.findAllByClientId(clientIdentification.clientId)
            .map { findAllResult -> Serializer.FindAllResponse(findAllResult) }

    @PostMapping
    fun getTransactionInfo(@RequestBody transactionInfo: TransactionInfo): Mono<TransactionResponse> =
        transactionQueryService.findById(transactionInfo)
            .map { findAllResult -> Serializer.FindResponse(findAllResult) }
}