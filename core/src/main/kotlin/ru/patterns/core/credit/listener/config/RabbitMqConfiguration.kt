package ru.patterns.core.credit.listener.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.patterns.core.config.MqProperties


@Configuration
@EnableRabbit
class RabbitMqConfiguration(
    private val mqProperties: MqProperties
) {
    @Bean
    fun topicExchange() = TopicExchange(mqProperties.exchange)

    @Bean
    fun queues(): Map<String, Queue> =
        mqProperties.queues.entries
            .associate { entry -> entry.key to Queue(entry.value) }

    @Bean
    fun bindings(exchange: TopicExchange, queues: Map<String, Queue>): Map<String, Binding> =
        mqProperties.routingKeys.entries
            .associate { entry ->
                entry.key to BindingBuilder
                    .bind(queues[entry.key])
                    .to(exchange)
                    .with(entry.value)
            }
}