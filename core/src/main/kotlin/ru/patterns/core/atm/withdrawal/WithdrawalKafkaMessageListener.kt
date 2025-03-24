package ru.patterns.core.atm.withdrawal

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.patterns.core.atm.AtmKafkaMessageListener
import ru.patterns.core.atm.withdrawal.serialization.WithdrawalRaw
import ru.patterns.core.config.AtmProperties
import ru.patterns.core.service.kafka.listener.RawMessageProcessor

@Component
class WithdrawalKafkaMessageListener(
    @Qualifier("withdrawalKafkaReceiver") kafkaReceiver: KafkaReceiver<String, String>,
    rawMessageProcessor: RawMessageProcessor<WithdrawalRaw>,
    atmProperties: AtmProperties
) : AtmKafkaMessageListener<WithdrawalRaw>(
    kafkaReceiver = kafkaReceiver,
    rawMessageProcessor = rawMessageProcessor,
    atmProperties = atmProperties
)