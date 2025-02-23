package ru.patterns.credit.infrastructure.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.CreateCreditCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditEventPublisher;
import ru.patterns.credit.shared.request.CreateCreditAccountRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditCommandHandler {
    private final CreditRepository creditRepository;
    private final CreditEventPublisher eventPublisher;

    public UUID handle(CreateCreditCommand command) {
        var createCreditAccountRequest = new CreateCreditAccountRequest(command.clientId(), command.amount());
        var accountData = eventPublisher.publishCreditCreation(createCreditAccountRequest);

        var credit = new Credit();
        credit.setClientId(command.clientId());
        credit.setAccountId(accountData.id());
        credit.setAmount(command.amount());
        var newCredit = creditRepository.save(credit);
        return newCredit.getId();
    }
}
