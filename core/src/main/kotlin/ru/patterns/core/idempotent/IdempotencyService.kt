package ru.patterns.core.idempotent

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit


@Service
class IdempotencyService(
    private val redisTemplate: RedisTemplate<String?, String?>
) {
    fun <T> executeOperation(operationId: String?, operation: () -> T): T? {
        if (operationId == null) {
            return operation()
        }

        val idempotencyKey = IDEMPOTENCY_KEY_PREFIX + operationId

        if (redisTemplate.hasKey(idempotencyKey)) {
            return null
        }

        val result: T = operation()

        redisTemplate.opsForValue().set(idempotencyKey, "executed", 1, TimeUnit.HOURS)

        return result
    }

    companion object {
        private const val IDEMPOTENCY_KEY_PREFIX = "idempotency:"
        const val IDEMPOTENCY_KEY = "X-idempotency-key"
    }
}