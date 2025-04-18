package ru.patterns.corequery.utils.metric

import org.springframework.stereotype.Component
import ru.patterns.corequery.utils.metric.serialization.MetricDto

@Component
class MetricRegistry(
    private val kafkaMetricSender: KafkaMetricSender
) {
    fun register(metricDto: MetricDto) {
        kafkaMetricSender.sendObjectToKafkaAsync(metricDto)
    }
}