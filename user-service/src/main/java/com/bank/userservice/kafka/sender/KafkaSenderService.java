package com.bank.userservice.kafka.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaSenderService<T> implements SenderService<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    @Override
    public void sendEvent(String topic, T event) {
        kafkaTemplate.send(topic, event)
                .handle((result, ex) -> {
                    if (ex != null) {
                        log.error("Error sending event to Kafka topic: {} - {}", topic, ex.getMessage());
                    } else {
                        log.info("Event sent to Kafka topic: {}", topic);
                    }
                    return null;
                });
    }
}

