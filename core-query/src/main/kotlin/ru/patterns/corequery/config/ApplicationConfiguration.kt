package ru.patterns.corequery.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.ResponseEntity
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import ru.patterns.corequery.utils.log.CustomLogger
import ru.patterns.corequery.utils.log.KafkaLogSender
import ru.patterns.corequery.utils.metric.Counter
import ru.patterns.corequery.utils.metric.MetricRegistry
import java.time.LocalDateTime
import java.util.UUID

@Configuration
@ComponentScan(
    basePackages = [
        "ru.patterns.corequery.service",
        "ru.patterns.corequery.security",
        "ru.patterns.corequery.utils"
    ]
)
class ApplicationConfiguration {
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    @Configuration
    class KafkaConfiguration(
        private val springKafkaProperties: KafkaProperties
    ) {
        @Bean
        fun kafkaSender(): KafkaSender<String, String> = KafkaSender.create(producerConfigs())

        fun producerConfigs(): SenderOptions<String, String> {
            val props = mapOf<String, Any>(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to springKafkaProperties.bootstrapServers,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.ACKS_CONFIG to "all",
                JsonSerializer.ADD_TYPE_INFO_HEADERS to false
            )

            return SenderOptions.create(props)
        }
    }
}

@Aspect
@Component
class RandomErrorAspect(
    private val randomErrorMetric: RandomErrorMetric,
    kafkaLogSender: KafkaLogSender
) {
    private val log = CustomLogger(
        clazz = this::class.java,
        sender = kafkaLogSender
    )

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    fun aroundRestControllerMethod(joinPoint: ProceedingJoinPoint): Any? =
        if (shouldReturnError()) {
            log.error(
                message = "Вас победил рандом",
                methodName = joinPoint.signature.name
            )
            randomErrorMetric.incrementError(UUID.randomUUID().toString())
            handleErrorCase(joinPoint)
        } else {
            randomErrorMetric.incrementSuccess(UUID.randomUUID().toString())
            joinPoint.proceed()
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

@Component
class RandomErrorMetric(
    private val metricRegistry: MetricRegistry
) {
    fun incrementError(requestId: String) {
        Counter
            .builder("core.query.random.error")
            .requestId(requestId)
            .register(metricRegistry)
    }

    fun incrementSuccess(requestId: String) {
        Counter
            .builder("core.query.random.success")
            .requestId(requestId)
            .register(metricRegistry)
    }
}

@Aspect
@Component
class ExecutionTimeLoggingAspect(
    kafkaLogSender: KafkaLogSender
) {
    private val log = CustomLogger(
        clazz = this::class.java,
        sender = kafkaLogSender
    )

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()

        val result = joinPoint.proceed()

        val executionTime = System.currentTimeMillis() - startTime
        log.methodExecutionTime(
            executionTimeMs = executionTime,
            methodName = joinPoint.signature.name,
        )

        return result
    }
}