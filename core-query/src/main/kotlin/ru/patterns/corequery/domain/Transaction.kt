package ru.patterns.corequery.domain

import java.time.LocalDateTime
import java.util.UUID

/**
 * Транзакция
 */
data class Transaction(
    /**
     * Идентификатор транзакции
     */
    val id: UUID,
    /**
     * Время совершения транзакции
     */
    val transactionTimestamp: LocalDateTime,
    /**
     * Идентификатор счета, откуда списываются средства,
     * может быть null, в случае, если происходит пополнение счета
     */
    val accountFrom: UUID?,
    /**
     * Идентификатор счета, куда переводятся средства
     */
    val accountTo: UUID,
    /**
     * Сумма, которую требуется перевести
     */
    val amount: Balance
)