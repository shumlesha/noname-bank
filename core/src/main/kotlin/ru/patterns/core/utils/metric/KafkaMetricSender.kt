package ru.patterns.core.utils.metric

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import ru.patterns.core.utils.KafkaAsyncSender
import ru.patterns.core.utils.metric.serialization.MetricDto

@Component
class KafkaMetricSender(
    kafkaSender: KafkaSender<String, String>,
    objectMapper: ObjectMapper,
) : KafkaAsyncSender<MetricDto>(
    kafkaSender = kafkaSender,
    objectMapper = objectMapper,
    topic = "metrics-topic"
)