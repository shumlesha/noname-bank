package ru.patterns.core.utils.metric

import ru.patterns.core.utils.metric.serialization.MetricDto
import java.time.Instant
import java.util.UUID

interface Counter {
    companion object {
        fun builder(metricName: String): Builder = Builder(metricName)

        class Builder(
            private val name: String
        ) {
            private var requestId: String? = null

            fun requestId(requestId: String): Builder {
                this.requestId = requestId
                return this
            }

            fun register(metricRegistry: MetricRegistry) {
                metricRegistry.register(
                    MetricDto(
                        requestId = requestId ?: UUID.randomUUID().toString(),
                        metricName = name,
                        timestamp = Instant.now().toEpochMilli()
                    )
                )
            }
        }
    }
}