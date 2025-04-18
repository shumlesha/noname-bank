package com.bank.notificationservice.service.kafka.sender;

import com.bank.notificationservice.dto.kafka.TraceDto;
import com.bank.notificationservice.service.kafka.KafkaAsyncSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLogSender extends KafkaAsyncSender<TraceDto> {
    public KafkaLogSender(KafkaTemplate<String, String> template,
                          ObjectMapper objectMapper) {
        super(template, objectMapper, "trace-logs-topic");
    }
}
