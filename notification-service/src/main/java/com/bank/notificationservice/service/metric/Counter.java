package com.bank.notificationservice.service.metric;

import com.bank.notificationservice.dto.kafka.MetricDto;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
