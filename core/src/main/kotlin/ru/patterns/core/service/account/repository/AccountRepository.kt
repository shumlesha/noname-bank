package ru.patterns.core.service.account.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.patterns.core.service.account.entity.AccountEntity
import java.util.UUID

@Repository
interface AccountRepository : R2dbcRepository<AccountEntity, UUID> {
    fun findAllByClientId(clientId: UUID): Mono<List<AccountEntity>>
}