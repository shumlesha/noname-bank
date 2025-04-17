package ru.patterns.monitoring.message;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.patterns.monitoring.model.MetricDto;
import ru.patterns.monitoring.model.TraceLogsDto;
import ru.patterns.monitoring.service.MetricService;

@Component
@RequiredArgsConstructor
public class MetricKafkaListener {

    private final MetricService metricService;

    @KafkaListener(
            topics = "trace-logs-topic",
            groupId = "trace-logs-group",
            containerFactory = "traceLogsKafkaListenerFactory"
    )
    public void consumeTraceMetric(TraceLogsDto dto) {
        metricService.saveTraceLogs(dto);
    }

    @KafkaListener(
            topics = "metrics-topic",
            groupId = "metrics-group",
            containerFactory = "metricKafkaListenerFactory"
    )
    public void consumeMetric(MetricDto dto) {
        metricService.saveMetric(dto);
    }
}
