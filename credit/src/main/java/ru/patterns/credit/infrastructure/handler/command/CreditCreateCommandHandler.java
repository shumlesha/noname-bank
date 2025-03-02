package ru.patterns.credit.infrastructure.handler.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.command.CreateCreditCommand;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.handler.command.serialization.CreditFactory;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditCreateEventPublisher;
import ru.patterns.credit.shared.exception.CreditProcessingException;
import ru.patterns.credit.shared.exception.InternalServerException;
import ru.patterns.credit.shared.response.credit.create.CreateCreditAccountResponseRaw;
import ru.patterns.credit.shared.request.credit.create.CreateCreditAccountRequest;
import ru.patterns.credit.shared.response.credit.create.CreateCreditAccountResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditCreateCommandHandler {
    private final CreditRepository creditRepository;
    private final CreditCreateEventPublisher eventPublisher;
    private final CreditFactory creditFactory;
    private final ObjectMapper objectMapper;

    @Transactional
    public UUID handle(CreateCreditCommand command) {
        try {
            var accountData = requestCreditAccount(command);

            if (accountData instanceof CreateCreditAccountResponse responseMessage){
                var credit = creditFactory.createCredit(command, responseMessage.account());
                creditRepository.save(credit);
                return credit.getId();
            } else {
                throw new CreditProcessingException("Ошибка при создании кредита");
            }
        } catch (IOException e) {
            throw new InternalServerException("Ошибка создания кредитного счета", e);
        }
    }

    private CreateCreditAccountResponseRaw requestCreditAccount(CreateCreditCommand command) throws IOException {
        var request = new CreateCreditAccountRequest(command.clientId(), command.amount());
        var eventResponse = Optional.ofNullable(eventPublisher.publishCreditCreation(request))
                .orElseThrow(() -> new CreditProcessingException("Ответ с кредитным счетом не получен"));

        var responseBody = new String(eventResponse.getBody(), StandardCharsets.UTF_8);
        var account = objectMapper.readValue(eventResponse.getBody(), CreateCreditAccountResponseRaw.class);
        log.info("Получен аккаунт {}", responseBody);

        return account;
    }
}
