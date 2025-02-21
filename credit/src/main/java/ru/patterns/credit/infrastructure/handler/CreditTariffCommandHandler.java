package ru.patterns.credit.infrastructure.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.command.CreateCreditTariffCommand;
import ru.patterns.credit.application.command.DeleteCreditTariffCommand;
import ru.patterns.credit.application.command.UpdateCreditTariffCommand;
import ru.patterns.credit.domain.model.CreditTariff;
import ru.patterns.credit.domain.repository.CreditTariffRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditTariffCommandHandler {
    private final CreditTariffRepository creditTariffRepository;

    @Transactional
    public UUID handle(CreateCreditTariffCommand command) {
        var creditTariff = new CreditTariff(UUID.randomUUID(), command.name(), command.interestRate());
        creditTariffRepository.save(creditTariff);
        return creditTariff.getId();
    }

    @Transactional
    public void handle(UpdateCreditTariffCommand command) {
        var creditTariff = creditTariffRepository.findById(command.tariffId())
                .orElseThrow(() -> new IllegalArgumentException("Тариф не найден"));
        creditTariff.setName(command.name());
        creditTariff.setInterestRate(command.interestRate());
        creditTariffRepository.save(creditTariff);
    }

    @Transactional
    public void handle(DeleteCreditTariffCommand command) {
        if (!creditTariffRepository.existsById(command.tariffId())) {
            throw new IllegalArgumentException("Тариф не найден");
        }
        creditTariffRepository.deleteById(command.tariffId());
    }
}

