package ru.patterns.atm.domain

import java.math.BigDecimal
import java.util.UUID

@JvmInline
value class Amount(val value: BigDecimal)

@JvmInline
value class AccountId(val value: UUID)