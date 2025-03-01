package ru.patterns.corequery.controller.transaction.serialization

data class FindAllTransactionResponse(
    val transactions: List<FindTransactionResponse>
) : TransactionResponse
