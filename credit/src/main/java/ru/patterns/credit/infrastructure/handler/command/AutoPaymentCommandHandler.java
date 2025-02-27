package ru.patterns.credit.infrastructure.handler.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.AutoPaymentCommand;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditEventPublisher;
import ru.patterns.credit.shared.request.PayCreditRequest;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AutoPaymentCommandHandler {

    private final CreditRepository creditRepository;
    private final CreditEventPublisher eventPublisher;

    @Transactional
    public void handle(AutoPaymentCommand command) {
        var credit = creditRepository.findById(command.creditId())
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));

        var payCreditRequest = new PayCreditRequest(credit.getAccountId(), command.amount());
        eventPublisher.publishPaymentRequest(payCreditRequest);

        credit.addPayment(command.amount());
        credit.setNextPaymentDate(LocalDate.now().plusDays(1));
        creditRepository.save(credit);
    }
}
