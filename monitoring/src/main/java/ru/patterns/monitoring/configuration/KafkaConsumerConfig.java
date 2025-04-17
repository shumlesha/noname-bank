package ru.patterns.monitoring.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.patterns.monitoring.model.MetricDto;
import ru.patterns.monitoring.model.TraceLogsDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaPropertiesConfig kafkaProps;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TraceLogsDto> traceLogsKafkaListenerFactory() {
        return kafkaListenerContainerFactory(TraceLogsDto.class, kafkaProps.getGroup().getTrace().getName());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MetricDto> metricKafkaListenerFactory() {
        return kafkaListenerContainerFactory(MetricDto.class, kafkaProps.getGroup().getMetric().getName());
    }

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> valueType, String groupId) {
        Map<String, Object> consumerProps = consumerConfigs(groupId);

        JsonDeserializer<T> deserializer = new JsonDeserializer<>(valueType);
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer);
    }


    private <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(
            Class<T> valueType,
            String groupId
    ) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(valueType, groupId));
        return factory;
    }

    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProps.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return props;
    }
}


