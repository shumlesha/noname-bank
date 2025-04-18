package ru.patterns.core.utils.metric

import org.springframework.stereotype.Component
import ru.patterns.core.utils.metric.serialization.MetricDto

@Component
class MetricRegistry(
    private val kafkaMetricSender: KafkaMetricSender
) {
    fun register(metricDto: MetricDto) {
        kafkaMetricSender.sendObjectToKafkaAsync(metricDto)
    }
}