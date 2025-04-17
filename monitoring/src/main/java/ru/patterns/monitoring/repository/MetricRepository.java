package ru.patterns.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.monitoring.entity.MetricEntity;

public interface MetricRepository extends JpaRepository<MetricEntity, Long> {
}
