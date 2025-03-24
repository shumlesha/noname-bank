package ru.patterns.core.atm

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import reactor.kafka.receiver.KafkaReceiver
import reactor.util.retry.Retry
import ru.patterns.core.config.AtmProperties
import ru.patterns.core.service.kafka.listener.RawMessageProcessor
import java.time.Duration

abstract class AtmKafkaMessageListener<T>(
    private val kafkaReceiver: KafkaReceiver<String, String>,
    private val rawMessageProcessor: RawMessageProcessor<T>,
    atmProperties: AtmProperties
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val backoff = Retry.backoff(
        Long.MAX_VALUE,
        Duration.ofMillis(atmProperties.kafka.minBackoffDelayMs)
    )
        .doAfterRetry {
            log.warn("При подключении возникла ошибка, попытка переподключения: {}", it.totalRetries(), it.failure())
        }

    @EventListener(ApplicationStartedEvent::class)
    fun listen() {
        kafkaReceiver.receive()
            .retryWhen(backoff)
            .flatMap { record ->
                val value = record.value()
                log.info("Получено сообщение: {}", value)

                rawMessageProcessor.process(value)
                    .doFinally { record.receiverOffset().acknowledge() }
            }
            .subscribe()
    }
}