package com.bank.authservice.config.rabbit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitMQProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;

    private QueueConfig queueConfig;

    @Data
    public static class QueueConfig {
        private String queueName;
        private String exchange;
        private String routingKey;
    }
}
