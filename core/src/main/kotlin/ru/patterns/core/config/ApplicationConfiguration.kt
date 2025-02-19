package ru.patterns.core.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty("core.enabled", matchIfMissing = true)
@ComponentScan(basePackages = ["ru.patterns.core.database"])
class ApplicationConfiguration