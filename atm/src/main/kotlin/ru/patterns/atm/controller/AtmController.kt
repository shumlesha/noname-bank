package ru.patterns.atm.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.patterns.atm.controller.serialization.DepositRaw
import ru.patterns.atm.controller.serialization.Factory
import ru.patterns.atm.controller.serialization.WithdrawalRaw
import ru.patterns.atm.kafka.KafkaEventSender

@RestController
@RequestMapping("/api/atm")
class AtmController(
    private val kafkaEventSender: KafkaEventSender
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/deposit")
    fun deposit(@RequestBody @Valid depositRaw: DepositRaw) {
        val deposit = Factory.Deposit(depositRaw)
        log.info("Deposit: {}", deposit)

        kafkaEventSender.sendEventToKafkaAsync(deposit)
    }

    @PostMapping("/withdraw")
    fun withdraw(@RequestBody @Valid withdrawalRaw: WithdrawalRaw) {
        val withdrawal = Factory.Withdrawal(withdrawalRaw)
        log.info("Withdrawal: {}", withdrawal)

        kafkaEventSender.sendEventToKafkaAsync(withdrawal)
    }
}