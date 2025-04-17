package ru.patterns.monitoring.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka")
@Getter
@Setter
public class KafkaPropertiesConfig {
    private String bootstrapServers;
    private Group group;

    @Getter
    @Setter
    public static class Group {
        private Name metric;
        private Name trace;

        @Getter
        @Setter
        public static class Name {
            private String name;
        }
    }
}
