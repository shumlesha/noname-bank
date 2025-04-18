package com.bank.notificationservice.service.idempotency;

import com.bank.notificationservice.dto.transaction.TransactionEntry;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IdempotencyRecordInterceptor implements RecordInterceptor<String, TransactionEntry> {
    private static final String PREFIX = "kafka:idemp:";
    private static final Duration TTL = Duration.ofHours(24);

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public ConsumerRecord<String, TransactionEntry> intercept(ConsumerRecord<String, TransactionEntry> record,
                                                              Consumer<String, TransactionEntry> consumer) {
        String msgId = Optional.ofNullable(
                        record.headers().lastHeader("X-idempotency-key"))
                .map(header -> new String(header.value(), StandardCharsets.UTF_8))
                .orElse(record.topic() + "-" + record.partition() + "-" + record.offset());

        Boolean duplicate = redisTemplate.opsForValue().setIfAbsent(PREFIX + msgId, "1", TTL);

        return Boolean.TRUE.equals(duplicate)
                ? record
                : null;
    }
}
