package ru.patterns.corequery.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty("query.websocket.transaction.enabled", matchIfMissing = true)
@EnableConfigurationProperties(TransactionWebSocketProperties::class)
@ComponentScan(basePackages = ["ru.patterns.corequery.websocket.transaction"])
class TransactionWebSocketConfiguration

@ConfigurationProperties("query.websocket.transaction")
data class TransactionWebSocketProperties(
    val client: WebSocketPath,
    val employee: WebSocketPath,
    val applicationDestinationPrefix: String = "/app",
    val destinationPrefixes: List<String> = listOf("/topic", "/topic/transactions-by-account")
) {
    data class WebSocketPath(
        val path: String
    )
}