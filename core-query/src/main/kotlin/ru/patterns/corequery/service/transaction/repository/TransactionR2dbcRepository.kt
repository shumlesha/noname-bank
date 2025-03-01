package ru.patterns.corequery.service.transaction.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.patterns.corequery.service.transaction.serialization.TransactionEntity
import java.util.UUID


@Repository
interface TransactionR2dbcRepository : R2dbcRepository<TransactionEntity, UUID> {
    fun findAllByClientId(clientId: UUID): Flux<TransactionEntity>
    fun findByIdAndClientId(transactionId: UUID, clientId: UUID): Mono<TransactionEntity>

    @Query("SELECT * FROM transactions t WHERE t.account_from = :accountId OR t.account_to = :accountId")
    fun findAllByAccountId(accountId: UUID): Flux<TransactionEntity>

    @Modifying
    @Query(
        """
    INSERT INTO transactions 
        (id, transaction_timestamp, account_from, account_to, amount, client_id) 
    VALUES 
        (:#{#entity.id}, 
         :#{#entity.transactionTimestamp}, 
         :#{#entity.accountFrom}, 
         :#{#entity.accountTo}, 
         :#{#entity.amount},
         :#{#entity.clientId})
         ON CONFLICT (id) DO UPDATE SET 
        transaction_timestamp = EXCLUDED.transaction_timestamp,
        account_from = EXCLUDED.account_from,
        account_to = EXCLUDED.account_to,
        amount = EXCLUDED.amount,
        client_id = EXCLUDED.client_id
    """
    )
    fun save(entity: TransactionEntity): Mono<Int>
}