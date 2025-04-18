package ru.patterns.credit.shared.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricDto {
    private String metricName;
    private long timestamp;
}
