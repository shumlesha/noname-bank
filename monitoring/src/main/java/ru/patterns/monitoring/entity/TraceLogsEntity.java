package ru.patterns.monitoring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trace_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "response_time_millis")
    private long responseTimeMillis;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    @Column(name = "message")
    private String message;
}
