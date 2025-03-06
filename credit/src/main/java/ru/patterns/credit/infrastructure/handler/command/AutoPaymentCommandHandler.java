package ru.patterns.credit.infrastructure.handler.command;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.AutoPaymentCommand;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.model.CreditStatus;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.service.CreditPaymentService;
import ru.patterns.credit.shared.exception.InsufficientFundsException;
import ru.patterns.credit.shared.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                scheduleRetryPaymentAndSetPenalty(credit);
            }
        } catch (InsufficientFundsException e) {
            log.warn("Недостаточно средств для автоплатежа по кредиту {}: {}", credit.getId(), e.getMessage());
            scheduleRetryPaymentAndSetPenalty(credit);
        } catch (Exception e) {
            log.error("Ошибка при выполнении автоплатежа для кредита {}: {}", credit.getId(), e.getMessage(), e);
            scheduleRetryPaymentAndSetPenalty(credit);
        }
    }

    private void scheduleRetryPaymentAndSetPenalty(Credit credit) {
        var nextAttemptDate = LocalDate.now().plusDays(1);
        credit.setNextPaymentDate(nextAttemptDate);

        var penaltyRate = credit.getTariff().getPenaltyRate().divide(BigDecimal.valueOf(100));
        var penaltyAmount = credit.getAmount().multiply(penaltyRate);
        credit.setAmount(credit.getAmount().add(penaltyAmount));

        creditRepository.save(credit);
        log.info("Запланирована повторная попытка автоплатежа для кредита {} на {}. " +
                "К сумме долга добавлен штраф в размере {}", credit.getId(), nextAttemptDate, penaltyAmount);

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

        var autoPaymentRate = credit.getTariff().getAutoPaymentRate().divide(BigDecimal.valueOf(100));
        var autoPaymentAmount = credit.getAmount().multiply(autoPaymentRate);

        return autoPaymentAmount.min(remainingAmount);
    }

    private Credit getCredit(UUID creditId) {
        return creditRepository.findById(creditId)
                .orElseThrow(() -> new ResourceNotFoundException("Кредит не найден"));
    }
}
