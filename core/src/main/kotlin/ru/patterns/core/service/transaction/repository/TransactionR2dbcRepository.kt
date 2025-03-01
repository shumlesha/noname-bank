package ru.patterns.core.service.transaction.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import ru.patterns.core.service.transaction.entity.TransactionEntity
import java.util.UUID

@Repository
interface TransactionR2dbcRepository : R2dbcRepository<TransactionEntity, UUID>