package ru.patterns.core.database.account

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import ru.patterns.core.database.account.entity.AccountEntity
import java.util.UUID

@Repository
interface AccountRepository : R2dbcRepository<AccountEntity, UUID>