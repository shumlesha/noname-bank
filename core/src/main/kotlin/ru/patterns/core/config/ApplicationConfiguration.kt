package ru.patterns.core.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import org.springframework.cglib.proxy.Proxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.ResponseEntity
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.Collections.singleton


@Configuration
@ConditionalOnProperty("core.enabled", matchIfMissing = true)
@ComponentScan(
    basePackages = [
        "ru.patterns.core.service",
        "ru.patterns.core.controller",
    ]
)
@EnableConfigurationProperties(KafkaListenerProperties::class)
class ApplicationConfiguration {
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    @Configuration
    class CommandKafkaConfiguration(
        val springKafkaProperties: KafkaProperties,
        val kafkaListenerProperties: KafkaListenerProperties
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

        @Bean("transactionKafkaReceiver")
        fun transactionKafkaReceiver(): KafkaReceiver<String, String> =
            KafkaReceiver.create(transactionConsumerConfigs())

        private fun transactionConsumerConfigs(): ReceiverOptions<String, String> =
            ReceiverOptions
                .create<String, String>(consumerProps())
                .subscription(singleton(kafkaListenerProperties.transactionTopic.name))

        private fun consumerProps(): Map<String, Any> =
            mapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to springKafkaProperties.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to kafkaListenerProperties.groupId,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                JsonDeserializer.TRUSTED_PACKAGES to "*"
            )
    }
}

@ConfigurationProperties("core.kafka")
data class KafkaListenerProperties(
    val minBackoffDelayMs: Long = 20,
    val groupId: String = "default",
    val transactionTopic: TopicConfig
) {
    data class TopicConfig(
        val name: String
    )
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