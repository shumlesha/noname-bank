package ru.patterns.corequery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableR2dbcRepositories
@EnableWebFlux
//@EnableDiscoveryClient
class CoreEventHandlerApplication

fun main(args: Array<String>) {
    runApplication<CoreEventHandlerApplication>(*args)
}
