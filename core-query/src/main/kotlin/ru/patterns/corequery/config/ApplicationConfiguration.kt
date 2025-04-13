package ru.patterns.corequery.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Configuration
@ComponentScan(basePackages = ["ru.patterns.corequery.service", "ru.patterns.corequery.security"])
class ApplicationConfiguration {
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}

@Aspect
@Component
class RandomErrorAspect {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    fun aroundRestControllerMethod(joinPoint: ProceedingJoinPoint): Any? {
        return if (shouldReturnError()) {
            log.error("Вас победил рандом")
            handleErrorCase(joinPoint)
        } else {
            joinPoint.proceed()
        }
    }

    private fun shouldReturnError(): Boolean {
        val currentMinute = LocalDateTime.now().minute
        val errorProbability = if (currentMinute % 2 == 0) 0.9 else 0.5
        return Math.random() < errorProbability
    }

    private fun handleErrorCase(joinPoint: ProceedingJoinPoint): Any {
        val methodSignature = joinPoint.signature as MethodSignature
        val returnType = methodSignature.method.returnType

        return when {
            Mono::class.java.isAssignableFrom(returnType) -> {
                Mono.error<Any>(RuntimeException("Ошибка"))
            }

            ResponseEntity::class.java.isAssignableFrom(returnType) -> {
                ResponseEntity.internalServerError().body("Ошибка")
            }

            else -> throw RuntimeException("Ошибка")
        }
    }
}