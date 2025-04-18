package com.bank.notificationservice.service.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomErrorMetric {
    private final MetricRegistry metricRegistry;

    public void incrementError() {
        Counter
                .builder("notification.random.error")
                .register(metricRegistry);
    }

    public void incrementSuccess() {
        Counter
                .builder("notification.random.success")
                .register(metricRegistry);
    }
}
