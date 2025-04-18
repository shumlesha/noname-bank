package ru.patterns.core.utils.log.serialization

import java.time.Instant
import java.util.UUID

object Factory {
    fun TraceDto(
        requestId: String?,
        endpoint: String,
        responseTimeMs: Long? = null
    ): TraceDto =
        TraceDto(
            requestId = requestId ?: UUID.randomUUID().toString(),
            serviceName = "core",
            endpoint = endpoint,
            responseTimeMillis = responseTimeMs,
            timestamp = Instant.now().toEpochMilli()
        )
}