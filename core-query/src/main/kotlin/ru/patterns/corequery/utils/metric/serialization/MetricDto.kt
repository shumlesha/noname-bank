package ru.patterns.corequery.utils.metric.serialization

data class MetricDto(
    val requestId: String,
    val metricName: String,
    val timestamp: Long
)