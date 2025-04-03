package ru.patterns.core.service.account.command

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.domain.commands.account.CreateCreditAccountCommand
import ru.patterns.core.domain.commands.transaction.CreateTransactionCommand
import ru.patterns.core.domain.Account
import ru.patterns.core.domain.AccountId
import ru.patterns.core.domain.Balance
import ru.patterns.core.service.account.command.MasterAccountService.GiveCreditResponse
import ru.patterns.core.service.account.repository.AccountRepository
import ru.patterns.core.service.kafka.KafkaEventSender
import ru.patterns.core.service.transaction.command.TransactionCommandService
import java.math.BigDecimal

interface MasterAccountService {
    fun giveCredit(createCreditAccountCommand: CreateCreditAccountCommand): Mono<GiveCreditResponse>

    sealed interface GiveCreditResponse {
        data class Success(val creditAccount: Account) : GiveCreditResponse
        sealed interface Error : GiveCreditResponse {
            data object NotEnoughMoney : Error
            data class RepositoryError(val cause: Throwable) : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }
}

@Component
class MasterAccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val transactionCommandService: TransactionCommandService,
    private val kafkaEventSender: KafkaEventSender
) : MasterAccountService {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun giveCredit(createCreditAccountCommand: CreateCreditAccountCommand): Mono<GiveCreditResponse> =
        accountRepository.findMasterAccount()
            .flatMap { findResult ->
                when (findResult) {
                    is AccountRepository.FindAccountResult.Success -> {
                        giveCredit(findResult.account, createCreditAccountCommand)
                    }

                    is AccountRepository.FindAccountResult.Error.Unexpected -> {
                        GiveCreditResponse.Error.RepositoryError(findResult.cause).toMono()
                    }

                    else -> {
                        log.error("При проверке возможности кредитования произошла неизвестная ошибка")
                        GiveCreditResponse.Error.Unexpected(IllegalArgumentException("Произошла неизвестная ошибка"))
                            .toMono()
                    }
                }
            }
            .doOnError { TransactionAspectSupport.currentTransactionStatus().setRollbackOnly() }

    private fun giveCredit(
        masterAccount: Account,
        createCreditAccountCommand: CreateCreditAccountCommand
    ): Mono<GiveCreditResponse> =
        accountRepository.save(createCreditAccountCommand)
            .flatMap { saveResult ->
                when (saveResult) {
                    is AccountRepository.SaveAccountResult.Success ->
                        createCreditTransaction(
                            masterAccount = masterAccount,
                            accountTo = saveResult.account,
                            amount = createCreditAccountCommand.amount
                        )

                    is AccountRepository.SaveAccountResult.Error ->
                        GiveCreditResponse.Error.Unexpected(saveResult.cause).toMono()
                }
            }

    private fun createCreditTransaction(
        masterAccount: Account,
        accountTo: Account,
        amount: BigDecimal
    ): Mono<GiveCreditResponse> =
        Mono.just(
            CreateTransactionCommand(
                ownerId = masterAccount.clientId,
                accountFrom = AccountId(value = masterAccount.id.value),
                accountTo = AccountId(value = accountTo.id.value),
                amount = Balance(value = amount)
            )
        )
            .flatMap { createTransactionCommand -> transactionCommandService.create(createTransactionCommand) }
            .map { createResult ->
                when (createResult) {
                    is TransactionCommandService.CreateTransactionResult.Success ->
                        GiveCreditResponse.Success(accountTo.copy(balance = Balance(amount)))

                    is TransactionCommandService.CreateTransactionResult.Error.NotEnoughMoney ->
                        GiveCreditResponse.Error.NotEnoughMoney

                    is TransactionCommandService.CreateTransactionResult.Error ->
                        GiveCreditResponse.Error.Unexpected(IllegalArgumentException("Что-то пошло не так"))
                }
            }
}