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
public class TraceLogsDto {
    private String requestId;
    private String serviceName;
    private String endpoint;
    private long responseTimeMillis;
    private long timestamp;
}
