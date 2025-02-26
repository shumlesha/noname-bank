package ru.patterns.corequery.controller.models

class ErrorResponse(
    val message: String,
    val statusCode: Int
) : ApiResponse