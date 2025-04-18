package com.bank.userservice.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceDto {
    private String requestId;
    @JsonProperty("service_name")
    private static final String SERVICE_NAME = "notification";
    private String endpoint;
    private long responseTimeMillis;
    private final long timestamp = Instant.now().toEpochMilli();
}
