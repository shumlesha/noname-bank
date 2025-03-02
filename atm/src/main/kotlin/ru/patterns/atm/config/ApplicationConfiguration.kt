package ru.patterns.atm.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.support.serializer.JsonSerializer
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
@ConditionalOnProperty("atm.enabled", matchIfMissing = true)
@ComponentScan(
    basePackages = [
        "ru.patterns.atm.kafka",
        "ru.patterns.atm.controller"
    ]
)
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration {
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    @Configuration
    class CommandKafkaConfiguration(
        val springKafkaProperties: KafkaProperties
    ) {
        @Bean
        fun kafkaSender(): KafkaSender<String, String> = KafkaSender.create(producerConfigs())

        fun producerConfigs(): SenderOptions<String, String> {
            val props = mapOf<String, Any>(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to springKafkaProperties.bootstrapServers,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.ACKS_CONFIG to "all",
                JsonSerializer.ADD_TYPE_INFO_HEADERS to false
            )

            return SenderOptions.create(props)
        }
    }
}

@ConfigurationProperties("atm")
data class ApplicationProperties(
    val kafka: KafkaProperties
) {
    data class KafkaProperties(
        val depositTopic: TopicConfig,
        val withdrawalTopic: TopicConfig
    ) {
        data class TopicConfig(
            val name: String
        )
    }
}