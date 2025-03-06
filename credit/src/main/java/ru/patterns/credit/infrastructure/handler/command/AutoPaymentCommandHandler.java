package ru.patterns.credit.infrastructure.handler.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.AutoPaymentCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.model.CreditStatus;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditPayEventPublisher;
import ru.patterns.credit.infrastructure.service.CreditPaymentService;
import ru.patterns.credit.shared.exception.InsufficientFundsException;
import ru.patterns.credit.shared.exception.ResourceNotFoundException;
import ru.patterns.credit.shared.request.credit.pay.PayCreditRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoPaymentCommandHandler {
    private final CreditPaymentService creditPaymentService;
    private final CreditRepository creditRepository;

    @Transactional
    public void handle(AutoPaymentCommand command) {
        var credit = getCredit(command.creditId());

        if (credit.getStatus() == CreditStatus.PAID_OFF) {
            return;
        }
        
        try {
            var currentAmount = calculateAutoPayment(credit);
            if (currentAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }

            var success = creditPaymentService.processPayment(credit, currentAmount);
            
            if (success) {
                log.info("Автоплатеж для кредита {} успешно выполнен", credit.getId());
            } else {
                scheduleRetryPayment(credit);
            }
        } catch (InsufficientFundsException e) {
            log.warn("Недостаточно средств для автоплатежа по кредиту {}: {}", credit.getId(), e.getMessage());
            scheduleRetryPayment(credit);
        } catch (Exception e) {
            log.error("Ошибка при выполнении автоплатежа для кредита {}: {}", credit.getId(), e.getMessage(), e);
            scheduleRetryPayment(credit);
        }
    }

    private void scheduleRetryPayment(Credit credit) {
        var nextAttemptDate = LocalDate.now().plusDays(1);
        credit.setNextPaymentDate(nextAttemptDate);
        creditRepository.save(credit);
        log.info("Запланирована повторная попытка автоплатежа для кредита {} на {}", credit.getId(), nextAttemptDate);

        if (credit.getStatus() != CreditStatus.OVERDUE && 
            credit.getNextPaymentDate().isBefore(LocalDate.now())) {
            credit.setStatus(CreditStatus.OVERDUE);
            creditRepository.save(credit);
            log.warn("Кредит {} помечен как просроченный", credit.getId());
        }
    }

    private BigDecimal calculateAutoPayment(Credit credit) {
        var remainingAmount = credit.getAmountRemainingToPay();
        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        var annualRate = credit.getTariff().getInterestRate().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        var daysUntilNextPayment = ChronoUnit.DAYS.between(LocalDate.now(), credit.getNextPaymentDate());

        if (daysUntilNextPayment <= 0) {
            daysUntilNextPayment = 1;
        }

        var periodRate = annualRate.multiply(BigDecimal.valueOf(daysUntilNextPayment))
                .divide(BigDecimal.valueOf(365), RoundingMode.HALF_UP);

        var interest = remainingAmount.multiply(periodRate);

        int remainingPeriods = Math.min(12, 365 / (int)daysUntilNextPayment);
        var principalPayment = remainingAmount.divide(BigDecimal.valueOf(remainingPeriods), RoundingMode.HALF_UP);
        var totalPayment = principalPayment.add(interest);

        return totalPayment.min(remainingAmount.add(interest));
    }

    private Credit getCredit(UUID creditId) {
        return creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException("Кредит не найден"));
    }
}
