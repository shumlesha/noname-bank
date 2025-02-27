package ru.patterns.core.service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.Transaction

@Component
class KafkaEventSender(
    private val kafkaSender: KafkaSender<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun sendEventToKafkaAsync(account: Account) {
        val senderRecord = SenderRecord(account).toMono()

        kafkaSender.send(senderRecord).subscribe()
    }

    fun sendEventToKafkaAsync(transaction: Transaction) {
        val senderRecord = SenderRecord(transaction).toMono()

        kafkaSender.send(senderRecord).subscribe()
    }

    private fun SenderRecord(value: Account) =
        reactor.kafka.sender.SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                "ACCOUNT",
                objectMapper.writeValueAsString(value)
            ),
            null
        )

    private fun SenderRecord(value: Transaction) =
        reactor.kafka.sender.SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                "TRANSACTION",
                objectMapper.writeValueAsString(value)
            ),
            null
        )
}