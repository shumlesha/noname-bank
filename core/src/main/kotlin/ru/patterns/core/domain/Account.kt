package ru.patterns.core.domain

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Счет клиента
 */
data class Account(
    /**
     * Идентификатор счета
     */
    val id: UUID,
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
    val clientId: UUID,
    /**
     * Номер счета
     */
    val number: String,
    /**
     * Баланс счета
     */
    val balance: BigDecimal
)