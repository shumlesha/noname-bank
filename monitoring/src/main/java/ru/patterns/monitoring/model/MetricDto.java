package ru.patterns.monitoring.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class MetricDto {
    private String metricName;
    private long timestamp;

    @JsonCreator
    public MetricDto(
            @JsonProperty("metricName") String metricName,
            @JsonProperty("timestamp") long timestamp) {
        this.metricName = metricName;
        this.timestamp = timestamp;
    }
}

