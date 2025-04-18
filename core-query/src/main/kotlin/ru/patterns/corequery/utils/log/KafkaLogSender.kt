package ru.patterns.corequery.utils.log

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import ru.patterns.corequery.utils.KafkaAsyncSender
import ru.patterns.corequery.utils.log.serialization.TraceDto

@Component
class KafkaLogSender(
    kafkaSender: KafkaSender<String, String>,
    objectMapper: ObjectMapper,
) : KafkaAsyncSender<TraceDto>(
    kafkaSender = kafkaSender,
    objectMapper = objectMapper,
    topic = "trace-logs-topic"
)