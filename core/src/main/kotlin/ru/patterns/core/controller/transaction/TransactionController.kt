package ru.patterns.core.controller.transaction

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.patterns.core.controller.transaction.serialization.CreateTransactionCommandRaw
import ru.patterns.core.service.kafka.KafkaEventSender

@RestController
@RequestMapping("/api/transaction")
class TransactionController(
    private val kafkaEventSender: KafkaEventSender
) {
    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    fun createTransaction(@RequestBody @Valid createTransactionCommandRaw: CreateTransactionCommandRaw) =
        kafkaEventSender.sendEventToKafka(createTransactionCommandRaw)
            .map { "Транзакция обрабатывается" }
}