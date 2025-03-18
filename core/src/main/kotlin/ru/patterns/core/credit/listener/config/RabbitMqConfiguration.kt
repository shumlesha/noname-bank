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
    fun creditPaymentRequestQueue() = Queue(mqProperties.creditPaymentRequest.name, true)

    @Bean
    fun creditPaymentResponseQueue() = Queue(mqProperties.creditPaymentResponse.name, true)

    @Bean
    fun creditCreateRequestQueue() = Queue(mqProperties.creditCreateRequest.name, true)

    @Bean
    fun creditCreateResponseQueue() = Queue(mqProperties.creditCreateResponse.name, true)

    @Bean
    fun bindingCreditCreateRequest(): Binding =
        BindingBuilder
            .bind(creditCreateRequestQueue())
            .to(topicExchange())
            .with(mqProperties.creditCreateRequest.name)

    @Bean
    fun bindingCreditCreateResponse(): Binding =
        BindingBuilder.bind(creditCreateResponseQueue())
            .to(topicExchange())
            .with(mqProperties.creditCreateResponse.name)

    @Bean
    fun bindingCreditPaymentRequest(): Binding =
        BindingBuilder.bind(creditPaymentRequestQueue())
            .to(topicExchange())
            .with(mqProperties.creditPaymentRequest.name)

    @Bean
    fun bindingCreditPaymentResponse(): Binding =
        BindingBuilder.bind(creditPaymentResponseQueue())
            .to(topicExchange())
            .with(mqProperties.creditPaymentResponse.name)
}