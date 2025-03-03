package ru.patterns.credit.infrastructure.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.command.PayCreditCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.model.CreditStatus;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditPayEventPublisher;
import ru.patterns.credit.infrastructure.service.CreditPaymentService;
import ru.patterns.credit.shared.exception.ResourceNotFoundException;
import ru.patterns.credit.shared.request.credit.pay.PayCreditRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditPayCommandHandler {
    private final CreditPaymentService creditPaymentService;
    private final CreditRepository creditRepository;

    @Transactional
    public UUID handle(PayCreditCommand command) {
        var credit = getCredit(command.creditId());
        creditPaymentService.processPayment(credit, command.amount());
        return command.creditId();
    }

    private Credit getCredit(UUID creditId) {
        return creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException("Кредит не найден"));
    }
}

