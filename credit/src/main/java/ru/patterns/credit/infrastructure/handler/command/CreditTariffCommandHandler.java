package ru.patterns.credit.infrastructure.handler.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.command.CreateCreditTariffCommand;
import ru.patterns.credit.application.command.DeleteCreditTariffCommand;
import ru.patterns.credit.application.command.UpdateCreditTariffCommand;
import ru.patterns.credit.domain.model.CreditTariff;
import ru.patterns.credit.domain.repository.CreditTariffRepository;
import ru.patterns.credit.shared.exception.ResourceNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditTariffCommandHandler {
    private final CreditTariffRepository creditTariffRepository;

    public UUID handle(CreateCreditTariffCommand command) {
        var creditTariff = new CreditTariff();
        creditTariff.setName(command.name());
        creditTariff.setInterestRate(command.interestRate());
        creditTariff.setAutoPaymentRate(command.autoPaymentRate());
        creditTariff.setPenaltyRate(command.penaltyRate());
        var newCreditTariff = creditTariffRepository.save(creditTariff);
        return newCreditTariff.getId();
    }

    @Transactional
    public void handle(UpdateCreditTariffCommand command) {
        var creditTariff = creditTariffRepository.findById(command.tariffId())
                .orElseThrow(() -> new ResourceNotFoundException("Тариф не найден"));
        creditTariff.setName(command.name());
        creditTariff.setInterestRate(command.interestRate());
        creditTariff.setAutoPaymentRate(command.autoPaymentRate());
        creditTariff.setPenaltyRate(command.penaltyRate());
        creditTariffRepository.save(creditTariff);
    }

    @Transactional
    public void handle(DeleteCreditTariffCommand command) {
        if (!creditTariffRepository.existsById(command.tariffId())) {
            throw new ResourceNotFoundException("Тариф не найден");
        }
        creditTariffRepository.deleteById(command.tariffId());
    }
}

