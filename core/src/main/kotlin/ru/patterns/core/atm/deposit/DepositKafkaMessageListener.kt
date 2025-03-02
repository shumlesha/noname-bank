package ru.patterns.core.atm.deposit

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.patterns.core.atm.KafkaMessageListener
import ru.patterns.core.atm.RawMessageProcessor
import ru.patterns.core.atm.deposit.serialization.DepositRaw
import ru.patterns.core.config.AtmProperties

@Component
class DepositKafkaMessageListener(
    @Qualifier("depositKafkaReceiver") kafkaReceiver: KafkaReceiver<String, String>,
    rawMessageProcessor: RawMessageProcessor<DepositRaw>,
    atmProperties: AtmProperties
) : KafkaMessageListener<DepositRaw>(
    kafkaReceiver = kafkaReceiver,
    rawMessageProcessor = rawMessageProcessor,
    atmProperties = atmProperties
)