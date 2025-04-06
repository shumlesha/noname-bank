package ru.patterns.core.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration


@Configuration
@ConditionalOnProperty("core.security.enabled", matchIfMissing = true)
@ComponentScan(basePackages = ["ru.patterns.core.security"])
@EnableConfigurationProperties(SecurityProperties::class)
class SecurityConfiguration

@ConfigurationProperties("core.security")
data class SecurityProperties(
    val secretKey: String,
    val header: String
)