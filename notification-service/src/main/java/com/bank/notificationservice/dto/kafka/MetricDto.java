package com.bank.notificationservice.dto.kafka;

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
