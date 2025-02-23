package com.bank.userservice.kafka.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Validated
@Data
public class TopicsConfig {
    @NotEmpty
    private Map<String, TopicConfig> topics;

    @Data
    public static class TopicConfig {
        @NotEmpty
        private String name;
        private TopicType type;
    }

    public enum TopicType {
        CONSUME,
        PRODUCE
    }

    public List<String> getByType(TopicType type) {
        return topics.values().stream()
                .filter(topicConfig -> topicConfig.getType() == type)
                .map(TopicConfig::getName)
                .toList();
    }
}
