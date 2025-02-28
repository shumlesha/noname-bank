package ru.patterns.corequery.service.transaction.serialization

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Table(name = "transactions")
class TransactionEntity(
    @Id
    val id: UUID? = null,
    @Column(value = "transaction_timestamp")
    val transactionTimestamp: LocalDateTime = LocalDateTime.now(),
    @Column(value = "account_from")
    val accountFrom: UUID?,
    @Column(value = "account_to")
    val accountTo: UUID,
    @Column(value = "amount")
    val amount: BigDecimal,
    @Column(value = "client_id")
    val clientId: UUID
)