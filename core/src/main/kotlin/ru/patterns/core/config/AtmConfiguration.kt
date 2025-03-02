package ru.patterns.core.config

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
@ConditionalOnProperty("core.atm.enabled", matchIfMissing = true)
@ComponentScan(basePackages = ["ru.patterns.core.atm"])
@EnableConfigurationProperties(AtmProperties::class)
class AtmConfiguration {
    @Configuration
    class KafkaConfiguration(
        val springKafkaProperties: KafkaProperties,
        atmProperties: AtmProperties
    ) {
        private val kafkaProperties = atmProperties.kafka

        @Bean("withdrawalKafkaReceiver")
        fun withdrawalKafkaReceiver(): KafkaReceiver<String, String> = KafkaReceiver.create(withdrawalConsumerConfigs())

        private fun withdrawalConsumerConfigs(): ReceiverOptions<String, String> =
            ReceiverOptions
                .create<String, String>(consumerProps())
                .subscription(singleton(kafkaProperties.withdrawalTopic.name))

        @Bean("depositKafkaReceiver")
        fun depositKafkaReceiver(): KafkaReceiver<String, String> =
            KafkaReceiver.create(depositConsumerConfigs())

        private fun depositConsumerConfigs(): ReceiverOptions<String, String> =
            ReceiverOptions
                .create<String, String>(consumerProps())
                .subscription(singleton(kafkaProperties.depositTopic.name))

        private fun consumerProps(): Map<String, Any> =
            mapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to springKafkaProperties.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to kafkaProperties.groupId,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                JsonDeserializer.TRUSTED_PACKAGES to "*"
            )
    }
}

@ConfigurationProperties("core.atm")
data class AtmProperties(
    val kafka: KafkaProperties
) {
    data class KafkaProperties(
        val minBackoffDelayMs: Long = 20,
        val groupId: String = "default",
        val depositTopic: TopicConfig,
        val withdrawalTopic: TopicConfig
    ) {
        data class TopicConfig(
            val name: String
        )
    }
}