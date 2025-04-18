package com.bank.notificationservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaAsyncSender<T> {
    private final KafkaTemplate<String, String> template;
    private final ObjectMapper objectMapper;
    private final String topic;


    public void send(T payload) {
        try {
            template.send(topic, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            log.error("Error while converting payload to JSON", e);
        } catch (Exception e) {
            log.error("Error while sending message to Kafka", e);
        }
    }
}

