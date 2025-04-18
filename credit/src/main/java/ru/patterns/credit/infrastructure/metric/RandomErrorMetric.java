package ru.patterns.credit.infrastructure.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomErrorMetric {
    private final MetricRegistry metricRegistry;

    public void incrementError() {
        Counter
                .builder("credit.random.error")
                .register(metricRegistry);
    }

    public void incrementSuccess() {
        Counter
                .builder("credit.random.success")
                .register(metricRegistry);
    }
}
