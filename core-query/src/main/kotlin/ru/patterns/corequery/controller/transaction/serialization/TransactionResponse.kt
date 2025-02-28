package ru.patterns.corequery.controller.transaction.serialization

interface TransactionResponse

class ErrorResponse(
    val message: String,
    val statusCode: Int
) : TransactionResponse