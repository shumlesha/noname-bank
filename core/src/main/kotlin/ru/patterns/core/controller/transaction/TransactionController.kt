package ru.patterns.core.controller.transaction

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.patterns.core.commands.transaction.CreateTransactionCommand
import ru.patterns.core.controller.transaction.serialization.Factory
import ru.patterns.core.service.transaction.command.TransactionCommandService

@RestController
@RequestMapping("/api/transaction")
class TransactionController(
    private val transactionCommandService: TransactionCommandService
) {
    @PostMapping("/create")
    fun createTransaction(@RequestBody @Valid createTransactionCommand: CreateTransactionCommand) =

        transactionCommandService.create(createTransactionCommand)
            .map { result -> Factory.CreateTransactionResponse(result) }
}