package ru.patterns.corequery.service.account.query.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.patterns.corequery.service.account.query.serialization.AccountEntity
import java.util.UUID

@Repository
interface AccountR2dbcRepository : R2dbcRepository<AccountEntity, UUID> {
    fun findAllByClientId(clientId: UUID): Flux<AccountEntity>
    fun findByClientIdAndId(clientId: UUID, accountId: UUID): Mono<AccountEntity>

    @Query("SELECT * FROM accounts ORDER BY id LIMIT :size OFFSET :offset")
    fun findAllWithPagination(size: Int, offset: Int): Flux<AccountEntity>

    @Modifying
    @Query(
        """
    INSERT INTO accounts 
        (id, creation_timestamp, blocked_timestamp, client_id, number, balance, is_credit, closed_timestamp) 
    VALUES 
        (:#{#entity.id}, 
         :#{#entity.creationTimestamp}, 
         :#{#entity.blockedTimestamp}, 
         :#{#entity.clientId}, 
         :#{#entity.number}, 
         :#{#entity.balance}, 
         :#{#entity.isCredit},
         :#{#entity.closedTimestamp})
         ON CONFLICT (id) DO UPDATE SET 
        creation_timestamp = EXCLUDED.creation_timestamp,
        blocked_timestamp = EXCLUDED.blocked_timestamp,
        client_id = EXCLUDED.client_id,
        number = EXCLUDED.number,
        balance = EXCLUDED.balance,
        is_credit = EXCLUDED.is_credit,
        closed_timestamp = EXCLUDED.closed_timestamp
    """
    )
    fun save(entity: AccountEntity): Mono<Int>
}