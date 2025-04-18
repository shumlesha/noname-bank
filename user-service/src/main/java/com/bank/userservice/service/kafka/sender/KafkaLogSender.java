package com.bank.userservice.service.kafka.sender;

import com.bank.userservice.dto.kafka.TraceDto;
import com.bank.userservice.service.kafka.KafkaAsyncSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLogSender extends KafkaAsyncSender<TraceDto> {
    public KafkaLogSender(KafkaTemplate<String, Object> template,
                          ObjectMapper objectMapper) {
        super(template, objectMapper, "trace-logs-topic");
    }
}
