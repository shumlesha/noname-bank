package ru.patterns.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.patterns.core.config.ApplicationConfiguration

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfiguration::class])
class CoreApplication

fun main(args: Array<String>) {
    runApplication<CoreApplication>(*args)
}
