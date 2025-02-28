package ru.patterns.corequery.domain

import ru.patterns.corequery.domain.AccountId
import ru.patterns.corequery.domain.AccountNumber
import ru.patterns.corequery.domain.Balance
import ru.patterns.corequery.domain.ClientId
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
     * Дата и время закрытия счета. Равен 'null', если счет открыт
     */
    val closedTimestamp: LocalDateTime?,
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