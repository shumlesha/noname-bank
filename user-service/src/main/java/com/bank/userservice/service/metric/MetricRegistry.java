package com.bank.userservice.service.metric;

import com.bank.userservice.dto.kafka.MetricDto;
import com.bank.userservice.service.kafka.sender.KafkaMetricSender;
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
