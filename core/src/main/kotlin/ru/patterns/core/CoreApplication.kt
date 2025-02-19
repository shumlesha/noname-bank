package ru.patterns.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.web.reactive.config.EnableWebFlux
import ru.patterns.core.config.ApplicationConfiguration

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfiguration::class])
@EnableR2dbcRepositories
@EnableWebFlux
class CoreApplication

fun main(args: Array<String>) {
    runApplication<CoreApplication>(*args)
}
