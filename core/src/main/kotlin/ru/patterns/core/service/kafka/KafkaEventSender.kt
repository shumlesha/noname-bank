package ru.patterns.core.service.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderResult
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.domain.commands.transaction.CreateTransactionCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.ClientId
import ru.patterns.core.domain.Transaction
import ru.patterns.core.service.kafka.serialization.Serializer
import ru.patterns.core.service.kafka.serialization.TransactionQueryEntity

@Component
class KafkaEventSender(
    private val kafkaSender: KafkaSender<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun sendEventToKafkaAsync(account: Account) {
        val senderRecord = SenderRecord(account).toMono()

        kafkaSender.send(senderRecord).subscribe()
    }

    fun sendEventToKafkaAsync(clientId: ClientId, transaction: Transaction) {
        val queryTransaction = Serializer.TransactionQueryEntity(clientId, transaction)
        val senderRecord = SenderRecord(queryTransaction).toMono()

        kafkaSender.send(senderRecord).subscribe()
    }

    fun sendEventToKafka(command: CreateTransactionCommand): Flux<SenderResult<String>> {
        val senderRecord = SenderRecord(command).toMono()

        return kafkaSender.send(senderRecord)
    }

    private fun SenderRecord(value: CreateTransactionCommand) =
        reactor.kafka.sender.SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                "TRANSACTION_PROCESSING",
                objectMapper.writeValueAsString(value)
            ),
            null
        )

    private fun SenderRecord(value: Account) =
        reactor.kafka.sender.SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                "ACCOUNT",
                objectMapper.writeValueAsString(value)
            ),
            null
        )

    private fun SenderRecord(value: TransactionQueryEntity) =
        reactor.kafka.sender.SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                "TRANSACTION",
                objectMapper.writeValueAsString(value)
            ),
            null
        )
}