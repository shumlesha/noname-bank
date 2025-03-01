package ru.patterns.credit.infrastructure.handler.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.AutoPaymentCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditPayEventPublisher;
import ru.patterns.credit.infrastructure.service.CreditPaymentService;
import ru.patterns.credit.shared.request.credit.pay.PayCreditRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AutoPaymentCommandHandler {
    private final CreditPaymentService creditPaymentService;
    private final CreditRepository creditRepository;

    @Transactional
    public void handle(AutoPaymentCommand command) {
        var credit = creditRepository.findById(command.creditId())
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));

        var currentAmount = creditPaymentService.calculateAutoPayment(credit);
        creditPaymentService.processPayment(command.creditId(), currentAmount);
    }
}

