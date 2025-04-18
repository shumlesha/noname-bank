package ru.patterns.core.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

open class KafkaAsyncSender<T>(
    private val kafkaSender: KafkaSender<String, String>,
    private val objectMapper: ObjectMapper,
    private val topic: String
) {
    fun sendObjectToKafkaAsync(obj: T) {
        val senderRecord = SenderRecord(obj).toMono()

        kafkaSender.send(senderRecord).subscribe()
    }

    private fun SenderRecord(value: T) =
        SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                topic,
                objectMapper.writeValueAsString(value)
            ),
            null
        )
}