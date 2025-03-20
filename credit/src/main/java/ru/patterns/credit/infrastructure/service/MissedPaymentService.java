package ru.patterns.credit.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.model.MissedPayment;
import ru.patterns.credit.domain.repository.MissedPaymentRepository;
import ru.patterns.credit.shared.dto.MissedPaymentDto;
import ru.patterns.credit.shared.mapper.MissedPaymentMapper;
import ru.patterns.credit.shared.request.credit.pay.MissedPaymentCreateRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissedPaymentService {
    private final MissedPaymentRepository missedPaymentRepository;
    private final MissedPaymentMapper missedPaymentMapper;

    public MissedPayment create(Credit credit, MissedPaymentCreateRequest dto) {
        var missedPayment = new MissedPayment();
        missedPayment.setCredit(credit);
        missedPayment.setMissedDate(LocalDate.now());
        missedPayment.setDebt(dto.debt());
        missedPayment.setAmount(dto.amount());

        return missedPaymentRepository.save(missedPayment);
    }

    public List<MissedPaymentDto> getMissedPaymentsByClientId(UUID clientId) {
        var missedPayments = missedPaymentRepository.findByCreditClientId(clientId);
        return missedPaymentMapper.toDtoList(missedPayments);
    }
}

