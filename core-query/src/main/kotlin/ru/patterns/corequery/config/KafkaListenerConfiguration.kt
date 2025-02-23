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
        @Bean
        fun kafkaReceiver(): KafkaReceiver<String, String> = KafkaReceiver.create(consumerConfigs())

        fun consumerConfigs(): ReceiverOptions<String, String> {
            val props = mapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to springKafkaProperties.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to kafkaListenerProperties.groupId,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                JsonDeserializer.TRUSTED_PACKAGES to "*"
            )

            return ReceiverOptions
                .create<String, String>(props)
                .subscription(singleton(kafkaListenerProperties.accountTopic.name))
        }
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