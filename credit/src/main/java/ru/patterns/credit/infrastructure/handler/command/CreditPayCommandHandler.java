package ru.patterns.credit.infrastructure.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.command.PayCreditCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditPayEventPublisher;
import ru.patterns.credit.shared.request.credit.pay.PayCreditRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditPayCommandHandler {
    private final CreditRepository creditRepository;
    private final CreditPayEventPublisher eventPublisher;

    @Transactional
    public UUID handle(PayCreditCommand command) {
        var credit = getCredit(command.creditId());
        validateCredit(credit);
        processPayment(credit, command.amount());
        return credit.getId();
    }

    private Credit getCredit(UUID creditId) {
        return creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));
    }

    private void validateCredit(Credit credit) {
        if (credit.isPaidOff()) {
            throw new IllegalStateException("Кредит уже выплачен");
        }
    }

    private void processPayment(Credit credit, BigDecimal amount) {
        var payCreditRequest = new PayCreditRequest(credit.getAccountId(), amount);
        eventPublisher.publishPaymentRequest(payCreditRequest);
        credit.addPayment(amount);
        credit.setNextPaymentDate(LocalDate.now().plusDays(1));
        creditRepository.save(credit);
    }
}
