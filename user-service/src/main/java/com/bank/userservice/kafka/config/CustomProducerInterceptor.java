package com.bank.userservice.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class CustomProducerInterceptor implements ProducerInterceptor<String, Object> {
    private String apiKey;

    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> recordToSend) {
        try {
            recordToSend.headers().add(
                    new RecordHeader("X-API-Key", apiKey.getBytes(StandardCharsets.UTF_8))
            );

            return recordToSend;
        } catch (Exception e) {
            log.error("Error while adding headers to the record: {}", e.getMessage());
            return recordToSend;
        }
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            log.warn("Message send failed: {}", exception.getMessage());
        } else if (log.isTraceEnabled()) {
            log.trace("Message successfully sent to {}-{} at offset {}",
                    metadata.topic(), metadata.partition(), metadata.offset());
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
