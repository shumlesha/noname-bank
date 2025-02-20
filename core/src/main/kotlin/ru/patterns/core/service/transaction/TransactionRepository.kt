package ru.patterns.core.service.transaction

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import ru.patterns.core.service.transaction.entity.TransactionEntity
import java.util.UUID

@Repository
interface TransactionRepository : R2dbcRepository<TransactionEntity, UUID>