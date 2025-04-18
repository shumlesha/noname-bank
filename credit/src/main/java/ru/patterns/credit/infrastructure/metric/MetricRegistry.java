package ru.patterns.credit.infrastructure.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.patterns.credit.infrastructure.messaging.sender.KafkaMetricSender;
import ru.patterns.credit.shared.kafka.MetricDto;

@Component
@RequiredArgsConstructor
public class MetricRegistry {
    private final KafkaMetricSender kafkaMetricSender;

    public void register(MetricDto dto) {
        kafkaMetricSender.send(dto);
    }
}
