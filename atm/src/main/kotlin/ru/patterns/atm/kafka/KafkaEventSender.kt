package ru.patterns.atm.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono
import ru.patterns.atm.config.ApplicationProperties
import ru.patterns.atm.domain.Deposit
import ru.patterns.atm.domain.Withdrawal

@Component
class KafkaEventSender(
    private val kafkaSender: KafkaSender<String, String>,
    private val objectMapper: ObjectMapper,
    applicationProperties: ApplicationProperties
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val kafkaProperties = applicationProperties.kafka

    fun sendEventToKafkaAsync(deposit: Deposit) {
        val senderRecord = SenderRecord(deposit).toMono()

        sendEventAsync(senderRecord)
    }

    fun sendEventToKafkaAsync(withdrawal: Withdrawal) {
        val senderRecord = SenderRecord(withdrawal).toMono()

        sendEventAsync(senderRecord)
    }

    private fun sendEventAsync(senderRecord: Mono<SenderRecord<String, String, String>>) {
        kafkaSender.send(senderRecord)
            .doOnNext { log.debug("Сообщение отправлено") }
            .subscribe()
    }

    private fun SenderRecord(value: Withdrawal) =
        SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                kafkaProperties.withdrawalTopic.name,
                objectMapper.writeValueAsString(value)
            ),
            null
        )

    private fun SenderRecord(value: Deposit) =
        SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                kafkaProperties.depositTopic.name,
                objectMapper.writeValueAsString(value)
            ),
            null
        )
}