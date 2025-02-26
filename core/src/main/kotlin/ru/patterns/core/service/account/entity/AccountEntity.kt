package ru.patterns.core.service.account.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Table("accounts")
data class AccountEntity(
    @Id
    val id: UUID? = null,
    @Column("creation_timestamp")
    val creationTimestamp: LocalDateTime = LocalDateTime.now(),
    @Column("blocked_timestamp")
    val blockedTimestamp: LocalDateTime? = null,
    @Column("closed_timestamp")
    val closedTimestamp: LocalDateTime? = null,
    @Column("client_id")
    val clientId: UUID,
    @Column("number")
    val number: String,
    @Column("balance")
    val balance: BigDecimal = BigDecimal.ZERO,
    @Column("is_credit")
    val isCredit: Boolean
)