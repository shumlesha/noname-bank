package com.bank.notificationservice.service.metric;

import com.bank.notificationservice.dto.kafka.MetricDto;
import com.bank.notificationservice.service.kafka.sender.KafkaMetricSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricRegistry {
    private final KafkaMetricSender kafkaMetricSender;

    public void register(MetricDto dto) {
        kafkaMetricSender.send(dto);
    }
}
