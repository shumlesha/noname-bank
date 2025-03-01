package ru.patterns.corequery.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.util.Collections.singleton

@Configuration
@ConditionalOnProperty("query.kafka.listener.enabled", matchIfMissing = true)
@EnableConfigurationProperties(KafkaListenerProperties::class)
@ComponentScan(basePackages = ["ru.patterns.corequery.listener"])
class KafkaListenerConfiguration {
    @Configuration
    class KafkaConfiguration(
        val springKafkaProperties: KafkaProperties,
        val kafkaListenerProperties: KafkaListenerProperties
    ) {
        @Bean("accountKafkaReceiver")
        fun accountKafkaReceiver(): KafkaReceiver<String, String> = KafkaReceiver.create(accountConsumerConfigs())

        private fun accountConsumerConfigs(): ReceiverOptions<String, String> =
            ReceiverOptions
                .create<String, String>(consumerProps())
                .subscription(singleton(kafkaListenerProperties.accountTopic.name))

        @Bean("transactionKafkaReceiver")
        fun transactionKafkaReceiver(): KafkaReceiver<String, String> =
            KafkaReceiver.create(transactionConsumerConfigs())

        private fun transactionConsumerConfigs(): ReceiverOptions<String, String> =
            ReceiverOptions
                .create<String, String>(consumerProps())
                .subscription(singleton(kafkaListenerProperties.transactionTopic.name))

        private fun consumerProps(): Map<String, Any> =
            mapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to springKafkaProperties.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to kafkaListenerProperties.groupId,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                JsonDeserializer.TRUSTED_PACKAGES to "*"
            )
    }
}

@ConfigurationProperties("query.kafka")
data class KafkaListenerProperties(
    val minBackoffDelayMs: Long = 20,
    val groupId: String = "default",
    val accountTopic: TopicConfig,
    val transactionTopic: TopicConfig
) {
    data class TopicConfig(
        val name: String
    )
}