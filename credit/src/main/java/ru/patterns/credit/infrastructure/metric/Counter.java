package ru.patterns.credit.infrastructure.metric;

import ru.patterns.credit.shared.kafka.MetricDto;

import java.time.Instant;

public interface Counter {

    static Builder builder(String metricName) {
        return new Builder(metricName);
    }

    final class Builder {
        private final String metric;

        Builder(String metric) {
            this.metric = metric;
        }


        public void register(MetricRegistry registry) {
            registry.register(new MetricDto(
                    metric,
                    Instant.now().toEpochMilli()
            ));
        }
    }
}
