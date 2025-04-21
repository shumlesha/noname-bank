package ru.patterns.core.controller.transaction

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.controller.transaction.serialization.CreateTransactionCommandRaw
import ru.patterns.core.controller.transaction.serialization.Factory
import ru.patterns.core.idempotent.IdempotencyService
import ru.patterns.core.idempotent.IdempotencyService.Companion.IDEMPOTENCY_KEY
import ru.patterns.core.security.CurrentUser
import ru.patterns.core.service.kafka.KafkaEventSender

@RestController
@RequestMapping("/api/transaction")
class TransactionController(
    private val kafkaEventSender: KafkaEventSender,
    private val idempotencyService: IdempotencyService,
) {
    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    fun createTransaction(
        authentication: Mono<BearerTokenAuthentication>,
        @RequestBody @Valid createTransactionCommandRaw: CreateTransactionCommandRaw,
        @RequestHeader(IDEMPOTENCY_KEY, required = false) idempotencyKey: String?
    ) =
        idempotencyService.executeOperation(idempotencyKey) {
            authentication
                .map { it.principal }
                .map { principal ->
                    principal as CurrentUser
                    Factory.CreateTransactionCommand(principal, createTransactionCommandRaw)
                }
                .flatMap { kafkaEventSender.sendEventToKafka(it).toMono() }
                .map { "Транзакция обрабатывается" }
        } ?: Factory.idempotencyError(idempotencyKey).toMono()
}