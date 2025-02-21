package ru.patterns.corequery.domain

import java.time.LocalDateTime

/**
 * Счет клиента
 */
data class Account(
    /**
     * Идентификатор счета
     */
    val id: AccountId,
    /**
     * Дата и время открытия счета
     */
    val creationTimestamp: LocalDateTime,
    /**
     * Дата и время блокировки счета. Равен 'null', если счет не заблокирован
     */
    val blockedTimestamp: LocalDateTime?,
    /**
     * Идентификатор клиента
     */
    val clientId: ClientId,
    /**
     * Номер счета
     */
    val number: AccountNumber,
    /**
     * Баланс счета
     */
    val balance: Balance,
    /**
     * Является ли счет кредитным
     */
    val isCredit: Boolean
)