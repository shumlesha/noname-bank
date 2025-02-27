package ru.patterns.core.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty("core.credit.mq.enabled", matchIfMissing = true)
@EnableConfigurationProperties(MqProperties::class)
@ComponentScan(basePackages = ["ru.patterns.core.credit.listener"])
class MqConfiguration

@ConfigurationProperties(prefix = "core.credit.mq")
data class MqProperties(
    val exchange: String,
    val queues: Map<String, String>,
    val routingKeys: Map<String, String>,
    val creditCreateRequest: RoutingKey,
    val creditCreateResponse: RoutingKey
) {
    data class RoutingKey(
        val name: String
    )
}