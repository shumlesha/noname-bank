package ru.patterns.atm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class AtmApplication

fun main(args: Array<String>) {
    runApplication<AtmApplication>(*args)
}
