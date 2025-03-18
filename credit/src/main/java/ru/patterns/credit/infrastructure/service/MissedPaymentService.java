package ru.patterns.credit.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.patterns.credit.domain.model.MissedPayment;
import ru.patterns.credit.domain.repository.MissedPaymentRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissedPaymentService {
    private final MissedPaymentRepository missedPaymentRepository;

    public MissedPayment create(MissedPayment missedPayment) {
        return missedPaymentRepository.save(missedPayment);
    }
}
