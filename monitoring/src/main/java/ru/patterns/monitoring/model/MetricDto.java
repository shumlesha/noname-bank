package ru.patterns.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricDto {
    private String requestId;
    private String metricName;
    private long timestamp;
}
