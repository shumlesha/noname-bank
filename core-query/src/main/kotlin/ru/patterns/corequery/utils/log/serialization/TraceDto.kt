package ru.patterns.corequery.utils.log.serialization

data class TraceDto(
    val requestId: String,
    val serviceName: String,
    val endpoint: String,
    val responseTimeMillis: Long?,
    val timestamp: Long
)