package ru.patterns.corequery.listener.transaction

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.patterns.corequery.config.KafkaListenerProperties
import ru.patterns.corequery.domain.Transaction
import ru.patterns.corequery.listener.KafkaMessageListener
import ru.patterns.corequery.listener.RawMessageProcessor

@Component
class TransactionKafkaMessageListener(
    @Qualifier("transactionKafkaReceiver") kafkaReceiver: KafkaReceiver<String, String>,
    rawMessageProcessor: RawMessageProcessor<Transaction>,
    kafkaListenerProperties: KafkaListenerProperties
) : KafkaMessageListener<Transaction>(
    kafkaReceiver = kafkaReceiver,
    rawMessageProcessor = rawMessageProcessor,
    kafkaListenerProperties = kafkaListenerProperties
)