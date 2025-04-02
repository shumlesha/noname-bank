package ru.patterns.core.service.account

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.patterns.core.service.account.command.serialization.Factory
import ru.patterns.core.service.account.entity.AccountEntity
import ru.patterns.core.service.account.repository.AccountR2dbcRepository
import ru.patterns.core.service.kafka.KafkaEventSender
import java.math.BigDecimal
import java.util.UUID

@Component
class MasterAccountInitializer(
    private val repository: AccountR2dbcRepository,
    private val kafkaEventSender: KafkaEventSender
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val BANK_ID_STR = "00000000-0000-0000-0000-000000000000"
        const val MASTER_ACCOUNT_NUMBER = "MASTER"
        val BANK_ID: UUID = UUID.fromString(BANK_ID_STR)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        val masterAccount = repository.findMasterAccount(BANK_ID, MASTER_ACCOUNT_NUMBER).block()

        if (masterAccount == null) {
            log.info("Создаем мастер-счет")
            val newMasterAccount = AccountEntity(
                id = BANK_ID,
                clientId = BANK_ID,
                number = MASTER_ACCOUNT_NUMBER,
                balance = BigDecimal.ZERO,
                isCredit = false,
                currency = "RUR"
            )

            repository.saveMasterAccount(newMasterAccount)
                .map(Factory::Account)
                .doOnSuccess { account ->
                    log.info("Мастер-счет создан")
                    kafkaEventSender.sendEventToKafkaAsync(account)
                }
                .block()
        }
    }
}