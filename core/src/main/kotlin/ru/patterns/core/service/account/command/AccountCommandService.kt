package ru.patterns.core.service.account.command

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.commands.account.CloseAccountCommand
import ru.patterns.core.commands.account.CreateAccountCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.service.account.command.AccountCommandService.CloseAccountResult
import ru.patterns.core.service.account.command.AccountCommandService.CreateAccountResult
import ru.patterns.core.service.account.command.serialization.Factory
import ru.patterns.core.service.account.entity.AccountEntity
import ru.patterns.core.service.account.repository.AccountR2dbcRepository
import kotlin.random.Random

interface AccountCommandService {
    fun createAccount(createAccountCommand: CreateAccountCommand): Mono<CreateAccountResult>
    fun closeAccount(closeAccountCommand: CloseAccountCommand): Mono<CloseAccountCommand>

    sealed interface CreateAccountResult {
        data object Success : CreateAccountResult
        data class Error(val cause: Throwable) : CreateAccountResult
    }

    sealed interface CloseAccountResult {
        data object Success : CloseAccountResult
        sealed interface Error : CloseAccountResult {
            data class ErrorFromRepository() : Error
            data class UnexpectedError(val cause: Throwable) : Error
        }
    }
}

@Service
class AccountCommandServiceImpl(
    private val accountR2dbcRepository: AccountR2dbcRepository,
    private val kafkaSender: KafkaSender<String, String>,
    private val objectMapper: ObjectMapper
) : AccountCommandService {
    @Transactional
    override fun createAccount(createAccountCommand: CreateAccountCommand): Mono<CreateAccountResult> =
        Mono.just(
            AccountEntity(
                clientId = createAccountCommand.clientId,
                number = generate16DigitNumber(),
                isCredit = createAccountCommand.isCredit
            )
        )
            .flatMap { accountEntity -> accountR2dbcRepository.save(accountEntity) }
            .doOnSuccess { accountEntity ->
                sendEventToKafkaAsync(accountEntity)
            }
            .map<CreateAccountResult> { CreateAccountResult.Success }
            .onErrorResume { error ->
                CreateAccountResult.Error(error).toMono()
            }

    @Transactional
    override fun closeAccount(closeAccountCommand: CloseAccountCommand): Mono<CloseAccountCommand> =
        TODO()

    private fun isClosed(accountEntity: AccountEntity): Boolean =
        accountEntity.closedTimestamp != null

    private fun SenderRecord(value: Account) =
        SenderRecord.create<String?, String, String?>(
            ProducerRecord(
                "ACCOUNT_CHANGE_TOPIC",
                objectMapper.writeValueAsString(value)
            ),
            null
        )

    private fun sendEventToKafkaAsync(accountEntity: AccountEntity) {
        val account = Factory.Account(accountEntity)

        val senderRecord = SenderRecord(account).toMono()

        kafkaSender.send(senderRecord).subscribe()
    }

    private fun generate16DigitNumber(): String {
        val random = Random(System.currentTimeMillis())
        val number = StringBuilder()

        number.append(random.nextInt(1, 10))

        for (i in 1 until 16) {
            number.append(random.nextInt(0, 10))
        }

        return number.toString()
    }
}