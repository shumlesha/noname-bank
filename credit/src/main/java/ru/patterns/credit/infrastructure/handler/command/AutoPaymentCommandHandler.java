package ru.patterns.credit.infrastructure.handler.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.AutoPaymentCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditPayEventPublisher;
import ru.patterns.credit.shared.request.credit.pay.PayCreditRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AutoPaymentCommandHandler {

    private final CreditRepository creditRepository;
    private final CreditPayEventPublisher eventPublisher;

    @Transactional
    public void handle(AutoPaymentCommand command) {
        var credit = creditRepository.findById(command.creditId())
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));

        var currentAmount = calculateAutoPayment(credit);

        var payCreditRequest = new PayCreditRequest(credit.getAccountId(), currentAmount);
        eventPublisher.publishPaymentRequest(payCreditRequest);

        credit.addPayment(currentAmount);
        credit.setNextPaymentDate(LocalDate.now().plusDays(1));
        creditRepository.save(credit);
    }

    private BigDecimal calculateAutoPayment(Credit credit) {
        var remainingAmount = credit.getAmountRemainingToPay();
        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        var annualRate = credit.getTariff().getInterestRate().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        var daysUntilNextPayment = ChronoUnit.DAYS.between(LocalDate.now(), credit.getNextPaymentDate());
        if (daysUntilNextPayment <= 0) {
            throw new IllegalStateException("Некорректная дата следующего платежа");
        }

        var periodRate = annualRate.multiply(BigDecimal.valueOf(daysUntilNextPayment))
                .divide(BigDecimal.valueOf(365), RoundingMode.HALF_UP);

        var interest = remainingAmount.multiply(periodRate);
        var principalPayment = remainingAmount.divide(BigDecimal.valueOf(daysUntilNextPayment), RoundingMode.HALF_UP);
        var totalPayment = principalPayment.add(interest);

        return totalPayment.min(remainingAmount.add(interest));
    }

}
