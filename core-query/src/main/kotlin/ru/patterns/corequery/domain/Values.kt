package ru.patterns.core.domain

import java.math.BigDecimal
import java.util.UUID

@JvmInline
value class AccountId(val value: UUID)

@JvmInline
value class AccountNumber(val value: String)

@JvmInline
value class ClientId(val value: UUID)

@JvmInline
value class Balance(val value: BigDecimal)