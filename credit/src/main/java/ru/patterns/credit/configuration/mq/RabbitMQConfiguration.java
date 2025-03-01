package ru.patterns.credit.configuration.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQConfiguration {

    private final RabbitMQProperties properties;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(properties.getExchange());
    }

    @Bean
    public Map<String, Queue> queues() {
        return properties.getQueues().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new Queue(entry.getValue(), true)));
    }

    @Bean
    public Map<String, Binding> bindings(TopicExchange exchange, Map<String, Queue> queues) {
        return properties.getRoutingKeys().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> BindingBuilder.bind(queues.get(entry.getKey()))
                                .to(exchange)
                                .with(entry.getValue())
                ));
    }

    @Bean("rabbitCreateCreditTemplate")
    public RabbitTemplate rabbitCreateCreditTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new SimpleMessageConverter());
        rabbitTemplate.setReplyAddress(replyQueue().getName());
        rabbitTemplate.setReplyTimeout(10000);
        rabbitTemplate.setUseDirectReplyToContainer(false);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer replyListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(replyQueue());
        container.setMessageListener(rabbitCreateCreditTemplate(connectionFactory));
        return container;
    }

    @Bean
    public Queue replyQueue() {
        return new Queue("credit.create.response");
    }

    @Bean("rabbitPayCreditTemplate")
    public RabbitTemplate rabbitPayCreditTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new SimpleMessageConverter());
        rabbitTemplate.setReplyAddress(payCreditQueue().getName());
        rabbitTemplate.setReplyTimeout(10000);
        rabbitTemplate.setUseDirectReplyToContainer(false);
        return rabbitTemplate;
    }

    @Bean
    public Queue payCreditQueue() {
        return new Queue("credit.payment.response");
    }

    @Bean
    public SimpleMessageListenerContainer payCreditListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(payCreditQueue());
        container.setMessageListener(rabbitPayCreditTemplate(connectionFactory));
        return container;
    }
}


