package ru.patterns.corequery.utils.metric

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import ru.patterns.corequery.utils.KafkaAsyncSender
import ru.patterns.corequery.utils.metric.serialization.MetricDto

@Component
class KafkaMetricSender(
    kafkaSender: KafkaSender<String, String>,
    objectMapper: ObjectMapper,
) : KafkaAsyncSender<MetricDto>(
    kafkaSender = kafkaSender,
    objectMapper = objectMapper,
    topic = "metrics-topic"
)