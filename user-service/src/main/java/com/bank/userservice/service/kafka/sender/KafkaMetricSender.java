package com.bank.userservice.service.kafka.sender;

import com.bank.userservice.dto.kafka.MetricDto;
import com.bank.userservice.service.kafka.KafkaAsyncSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMetricSender extends KafkaAsyncSender<MetricDto> {

    public KafkaMetricSender(KafkaTemplate<String, Object> template,
                             ObjectMapper objectMapper) {
        super(template, objectMapper, "metrics-topic");
    }
}
