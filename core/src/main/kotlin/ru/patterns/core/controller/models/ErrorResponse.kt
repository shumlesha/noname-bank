package ru.patterns.core.controller.models

data class ErrorResponse(
    val message: String,
    val statusCode: Int
)