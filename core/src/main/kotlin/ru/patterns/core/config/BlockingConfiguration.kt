package ru.patterns.core.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(BlockingProperties::class)
@ConditionalOnProperty("core.blocking.enabled", matchIfMissing = true)
@ComponentScan(basePackages = ["ru.patterns.core.blocking"])
class BlockingConfiguration

/**
 * Конфигурация потока событий блокировок
 */
@ConfigurationProperties("core.blocking")
data class BlockingProperties(
    val kafkaProperties: KafkaProperties
) {
    /**
     * Настройки для работы с Kafka
     *
     * @property bootstrapServers url для соединения с кафкой
     * @property blockingTopic топик для потока блокировки
     */
    data class KafkaProperties(
        val bootstrapServers: String,
        val blockingTopic: TopicConfig,
    ) {
        data class TopicConfig(
            val name: String
        )
    }
}