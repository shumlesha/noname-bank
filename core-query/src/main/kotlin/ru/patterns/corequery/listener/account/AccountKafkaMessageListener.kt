package ru.patterns.corequery.listener.account

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import ru.patterns.corequery.config.KafkaListenerProperties
import ru.patterns.corequery.domain.Account
import ru.patterns.corequery.listener.KafkaMessageListener
import ru.patterns.corequery.listener.RawMessageProcessor

@Component
class AccountKafkaMessageListener(
    @Qualifier("accountKafkaReceiver") kafkaReceiver: KafkaReceiver<String, String>,
    rawMessageProcessor: RawMessageProcessor<Account>,
    kafkaListenerProperties: KafkaListenerProperties
) : KafkaMessageListener<Account>(
    kafkaReceiver = kafkaReceiver,
    rawMessageProcessor = rawMessageProcessor,
    kafkaListenerProperties = kafkaListenerProperties
)