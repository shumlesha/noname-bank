package ru.patterns.credit.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.credit.domain.model.MissedPayment;

import java.util.UUID;

public interface MissedPaymentRepository extends JpaRepository<MissedPayment, UUID> { }
