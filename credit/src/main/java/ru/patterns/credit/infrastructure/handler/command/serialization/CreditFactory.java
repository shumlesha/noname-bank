package ru.patterns.credit.infrastructure.handler.command.serialization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.CreateCreditCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.model.CreditStatus;
import ru.patterns.credit.domain.repository.CreditTariffRepository;
import ru.patterns.credit.shared.exception.ResourceNotFoundException;
import ru.patterns.credit.shared.response.credit.create.Account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditFactory {
    private final CreditTariffRepository creditTariffRepository;

    public Credit createCredit(CreateCreditCommand command, Account accountData) {
        var tariff = creditTariffRepository.findById(command.tariffId())
                .orElseThrow(() -> new ResourceNotFoundException("Кредитный тариф не найден"));

        return new Credit(
                UUID.randomUUID(),
                command.clientId(),
                accountData.id(),
                command.amount(),
                BigDecimal.ZERO,
                tariff,
                CreditStatus.ACTIVE,
                LocalDate.now().plusDays(1)
        );
    }
}
