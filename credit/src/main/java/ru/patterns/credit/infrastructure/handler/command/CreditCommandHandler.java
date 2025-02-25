package ru.patterns.credit.infrastructure.handler.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.command.CreateCreditCommand;
import ru.patterns.credit.application.command.PayCreditCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.handler.command.serialization.CreditFactory;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditEventPublisher;
import ru.patterns.credit.shared.CreateCreditAccountResponseRaw;
import ru.patterns.credit.shared.request.CreateCreditAccountRequest;
import ru.patterns.credit.shared.request.PayCreditRequest;
import ru.patterns.credit.shared.response.CreateCreditAccountResponse;
import ru.patterns.credit.shared.response.CreateCreditResponseMessage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditCommandHandler {
    private final CreditRepository creditRepository;
    private final CreditEventPublisher eventPublisher;
    private final CreditFactory creditFactory;
    private final ObjectMapper objectMapper;

    @Transactional
    public UUID handle(CreateCreditCommand command) {
        try {
            var accountData = requestCreditAccount(command);

            if (accountData instanceof CreateCreditResponseMessage responseMessage){
                var credit = creditFactory.createCredit(command, responseMessage.account());
                creditRepository.save(credit);
                return credit.getId();
            } else {
                throw new RuntimeException("error");
            }
        } catch (IOException e) {
            throw new RuntimeException("error");
        }
    }

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

    private CreateCreditAccountResponseRaw requestCreditAccount(CreateCreditCommand command) throws IOException {
        var request = new CreateCreditAccountRequest(command.clientId(), command.amount());
        var eventResponse = eventPublisher.publishCreditCreation(request);
        log.warn(eventResponse.getBody().toString());
        return objectMapper.readValue(eventResponse.getBody(), CreateCreditAccountResponseRaw.class);
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
