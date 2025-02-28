package ru.patterns.corequery.controller.account.serialization

interface AccountResponse

class ErrorResponse(
    val message: String,
    val statusCode: Int
) : AccountResponse