package ru.patterns.core.controller.transaction

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.patterns.core.controller.transaction.serialization.CreateTransactionCommandRaw
import ru.patterns.core.controller.transaction.serialization.Factory
import ru.patterns.core.controller.transaction.serialization.Serializer
import ru.patterns.core.service.transaction.command.TransactionCommandService

@RestController
@RequestMapping("/api/transaction")
class TransactionController(
    private val transactionCommandService: TransactionCommandService
) {
    @PostMapping("/create")
    fun createTransaction(@RequestBody @Valid createTransactionCommandRaw: CreateTransactionCommandRaw) =
        Mono.fromCallable { Factory.CreateTransactionCommand(createTransactionCommandRaw) }
            .flatMap { command -> transactionCommandService.create(command) }
            .map { result -> Serializer.CreateTransactionResponse(result) }
}