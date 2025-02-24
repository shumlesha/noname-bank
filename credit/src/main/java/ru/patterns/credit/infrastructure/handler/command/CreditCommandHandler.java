package ru.patterns.credit.infrastructure.handler.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.CreateCreditCommand;
import ru.patterns.credit.application.command.PayCreditCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.handler.command.serialization.CreditFactory;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditEventPublisher;
import ru.patterns.credit.shared.request.CreateCreditAccountRequest;
import ru.patterns.credit.shared.request.PayCreditRequest;
import ru.patterns.credit.shared.response.CreateCreditAccountResponse;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditCommandHandler {
    private final CreditRepository creditRepository;
    private final CreditEventPublisher eventPublisher;
    private final CreditFactory creditFactory;

    public UUID handle(CreateCreditCommand command) {
        var accountData = requestCreditAccount(command);
        var credit = creditFactory.createCredit(command, accountData);
        creditRepository.save(credit);

        return credit.getId();
    }

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

    private CreateCreditAccountResponse requestCreditAccount(CreateCreditCommand command) {
        var request = new CreateCreditAccountRequest(command.clientId(), command.amount());
        return eventPublisher.publishCreditCreation(request);
    }

    private void validateCredit(Credit credit) {
        if (creditIsFullyPaid(credit)) {
            throw new IllegalStateException("Кредит уже выплачен");
        }
    }

    private void processPayment(Credit credit, BigDecimal amount) {
        var payCreditRequest = new PayCreditRequest(credit.getAccountId(), amount);
        eventPublisher.publishPaymentRequest(payCreditRequest);
    }


    private boolean creditIsFullyPaid(Credit credit){
        return credit.getAmount().equals(credit.getPaidAmount());
    }
}
