package ru.patterns.credit.infrastructure.messaging.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.patterns.credit.infrastructure.messaging.KafkaAsyncSender;
import ru.patterns.credit.shared.kafka.MetricDto;

@Component
public class KafkaMetricSender extends KafkaAsyncSender<MetricDto> {

    public KafkaMetricSender(KafkaTemplate<String, Object> template,
                             ObjectMapper objectMapper) {
        super(template, objectMapper, "metrics-topic");
    }
}
