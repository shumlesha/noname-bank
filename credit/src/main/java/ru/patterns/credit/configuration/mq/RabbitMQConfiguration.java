package ru.patterns.credit.configuration.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
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
}

