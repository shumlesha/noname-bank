package com.bank.userservice.service.idempotency;

import com.bank.userservice.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    public static final String IDEMPOTENCY_KEY_HEADER = "X-idempotency-key";
    public static final String IDEMPOTENCY_KEY_PARAM = "idempotencyKey";

    private static final String PREFIX = "idempotency:";
    private static final Duration TTL = Duration.ofHours(1);

    private final RedisTemplate<String, String> redis;

    public <T> T execute(String idempotencyKey, Supplier<T> supplier) {
        String redisKey = PREFIX + idempotencyKey;
        Boolean already = redis.hasKey(redisKey);

        if (Boolean.TRUE.equals(already)) {
            throw new BadRequestException("Операция с ключом %s уже выполнена".formatted(idempotencyKey));
        }

        T result = supplier.get();
        redis.opsForValue().set(redisKey, "executed", TTL);
        return result;
    }
}
