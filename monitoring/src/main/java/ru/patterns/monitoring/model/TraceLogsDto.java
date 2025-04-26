package ru.patterns.monitoring.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class TraceLogsDto {
    private String requestId;
    private String serviceName;
    private String endpoint;
    private long responseTimeMillis;
    private long timestamp;
    private String message;

    @JsonCreator
    public TraceLogsDto(
            @JsonProperty("requestId") String requestId,
            @JsonProperty("serviceName") String serviceName,
            @JsonProperty("endpoint") String endpoint,
            @JsonProperty("responseTimeMillis") long responseTimeMillis,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("message") String message) {
        this.requestId = requestId;
        this.serviceName = serviceName;
        this.endpoint = endpoint;
        this.responseTimeMillis = responseTimeMillis;
        this.timestamp = timestamp;
        this.message = message;
    }
}

