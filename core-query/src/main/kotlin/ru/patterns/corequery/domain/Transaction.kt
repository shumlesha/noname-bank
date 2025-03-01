package ru.patterns.corequery.domain

import java.time.LocalDateTime

/**
 * Транзакция
 */
data class Transaction(
    /**
     * Идентификатор транзакции
     */
    val id: TransactionId,
    /**
     * Время совершения транзакции
     */
    val transactionTimestamp: LocalDateTime,
    /**
     * Идентификатор счета, откуда списываются средства,
     * может быть null, в случае, если происходит пополнение счета
     */
    val accountFrom: AccountId?,
    /**
     * Идентификатор счета, куда переводятся средства
     */
    val accountTo: AccountId,
    /**
     * Сумма, которую требуется перевести
     */
    val amount: Balance,
    /**
     * Владелец счета, с которого произведен перевод
     */
    val clientId: ClientId,
)