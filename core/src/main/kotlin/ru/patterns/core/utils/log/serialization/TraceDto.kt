package ru.patterns.core.utils.log.serialization

data class TraceDto(
    val requestId: String,
    val serviceName: String,
    val endpoint: String,
    val responseTimeMillis: Long? = null,
    val timestamp: Long
)