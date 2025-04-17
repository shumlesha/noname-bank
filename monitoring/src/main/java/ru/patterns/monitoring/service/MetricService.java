package ru.patterns.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.monitoring.entity.MetricEntity;
import ru.patterns.monitoring.entity.TraceLogsEntity;
import ru.patterns.monitoring.model.MetricDto;
import ru.patterns.monitoring.model.TraceLogsDto;
import ru.patterns.monitoring.repository.MetricRepository;
import ru.patterns.monitoring.repository.TraceLogsRepository;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final TraceLogsRepository traceMetricRepository;
    private final MetricRepository metricRepository;

    public void saveTraceLogs(TraceLogsDto dto) {
        var entity = TraceLogsEntity.builder()
                .requestId(dto.getRequestId())
                .serviceName(dto.getServiceName())
                .endpoint(dto.getEndpoint())
                .responseTimeMillis(dto.getResponseTimeMillis())
                .timestamp(dto.getTimestamp())
                .build();

        traceMetricRepository.save(entity);
    }

    public void saveMetric(MetricDto dto) {
        var entity = MetricEntity.builder()
                .requestId(dto.getRequestId())
                .timestamp(dto.getTimestamp())
                .build();

        metricRepository.save(entity);
    }
}
