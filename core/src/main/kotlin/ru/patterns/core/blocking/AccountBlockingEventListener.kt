package ru.patterns.core.blocking

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import reactor.util.retry.Retry
import ru.patterns.core.config.BlockingProperties
import java.time.Duration

@Component
class AccountBlockingEventListener(
    @Qualifier("blockingKafkaReceiver") private val kafkaReceiver: KafkaReceiver<String, String>,
    private val rawMessageProcessor: RawBlockingEventProcessor,
    blockingProperties: BlockingProperties
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val backoff = Retry.backoff(
        Long.MAX_VALUE,
        Duration.ofMillis(blockingProperties.kafkaProperties.minBackoffDelayMs)
    )
        .doAfterRetry {
            log.warn("При подключении возникла ошибка, попытка переподключения: {}", it.totalRetries(), it.failure())
        }

    @EventListener(ApplicationReadyEvent::class)
    fun listen() {
        kafkaReceiver.receive()
            .retryWhen(backoff)
            .flatMap { record ->
                val value = record.value()
                log.info("Пришло сообщение о блокировке пользователя: {}", value)

                rawMessageProcessor.processRawMessage(value)
                    .doFinally { record.receiverOffset().acknowledge() }
            }
            .subscribe()
    }
}