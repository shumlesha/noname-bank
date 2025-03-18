package ru.patterns.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Configuration
@EnableConfigurationProperties(CurrencyProperties::class)
class CurrencyConfiguration {
    @Bean
    fun webClient(): WebClient =
        WebClient.builder().build()
}

@ConfigurationProperties("core.currency")
data class CurrencyProperties(
    val api: ApiProperties,
    private val cacheLifetimeHours: Long = 1
) {
    val cacheLifeTimeDuration = Duration.ofHours(cacheLifetimeHours)

    data class ApiProperties(
        val currency: ApiConfig,
        val valute: ApiConfig
    ) {
        data class ApiConfig(
            val url: String
        )
    }
}