package ru.patterns.core.blocking.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import ru.patterns.core.config.BlockingProperties
import java.util.Collections.singleton

@Configuration
class KafkaConfiguration(
    val blockingProperties: BlockingProperties,
    val springKafkaProperties: KafkaProperties
) {
    @Bean
    fun blockingKafkaReceiver(): KafkaReceiver<String, String> = KafkaReceiver.create(consumerConfigs())

    fun consumerConfigs(): ReceiverOptions<String, String> {
        val props = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to springKafkaProperties.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to blockingProperties.kafkaProperties.groupId,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )

        return ReceiverOptions
            .create<String, String>(props)
            .subscription(singleton(blockingProperties.kafkaProperties.blockingTopic.name))
    }
}