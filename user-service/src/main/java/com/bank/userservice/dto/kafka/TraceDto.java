package com.bank.userservice.dto.kafka;

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
    private String serviceName = "user-service";
    private String endpoint;
    private long responseTimeMillis;
    private final long timestamp = Instant.now().toEpochMilli();
}
