package ru.patterns.core.service.kafka.listener.transaction

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.patterns.core.config.KafkaListenerProperties
import ru.patterns.core.controller.transaction.serialization.CreateTransactionCommandRaw
import ru.patterns.core.service.kafka.listener.KafkaMessageListener
import ru.patterns.core.service.kafka.listener.RawMessageProcessor

@Component
class TransactionKafkaMessageListener(
    @Qualifier("transactionKafkaReceiver") kafkaReceiver: KafkaReceiver<String, String>,
    rawMessageProcessor: RawMessageProcessor<CreateTransactionCommandRaw>,
    kafkaListenerProperties: KafkaListenerProperties
) : KafkaMessageListener<CreateTransactionCommandRaw>(
    kafkaReceiver = kafkaReceiver,
    rawMessageProcessor = rawMessageProcessor,
    kafkaListenerProperties = kafkaListenerProperties
)