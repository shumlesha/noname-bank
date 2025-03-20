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
import ru.patterns.credit.infrastructure.service.MissedPaymentService;
import ru.patterns.credit.shared.exception.InsufficientFundsException;
import ru.patterns.credit.shared.exception.ResourceNotFoundException;
import ru.patterns.credit.shared.request.credit.pay.MissedPaymentCreateRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoPaymentCommandHandler {
    private static final int RETRY_DELAY_DAYS = 1;
    private final CreditPaymentService creditPaymentService;
    private final MissedPaymentService missedPaymentService;
    private final CreditRepository creditRepository;

    @Transactional
    public void handle(AutoPaymentCommand command) {
        getCreditOptional(command.creditId())
                .filter(credit -> credit.getStatus() != CreditStatus.PAID_OFF)
                .ifPresent(credit -> {
                    var amount = calculateAutoPayment(credit);

                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        var paymentResult = creditPaymentService.processPayment(credit, amount);

                        if ("success".equals(paymentResult.status())) {
                            log.info("Автоплатеж для кредита {} успешно выполнен", credit.getId());
                        } else {
                            handleFailedPayment(credit);
                            missedPaymentService.create(
                                    credit,
                                    new MissedPaymentCreateRequest(paymentResult.debt(), amount)
                            );
                        }
                    }
                });
    }

    private void handleFailedPayment(Credit credit) {
        scheduleRetryPayment(credit);
        applyPenalty(credit);

        if (credit.getStatus() != CreditStatus.OVERDUE &&
                credit.getNextPaymentDate().isBefore(LocalDate.now())) {
            credit.setStatus(CreditStatus.OVERDUE);
            log.warn("Кредит {} помечен как просроченный", credit.getId());
            creditRepository.save(credit);
        }
    }

    private void scheduleRetryPayment(Credit credit) {
        var nextAttemptDate = LocalDate.now().plusDays(RETRY_DELAY_DAYS);
        credit.setNextPaymentDate(nextAttemptDate);
        log.info("Запланирована повторная попытка автоплатежа для кредита {} на {}", credit.getId(), nextAttemptDate);
    }

    private void applyPenalty(Credit credit) {
        var penaltyRate = credit.getTariff().getPenaltyRate().divide(BigDecimal.valueOf(100));
        var penaltyAmount = credit.getAmount().multiply(penaltyRate);
        credit.setAmount(credit.getAmount().add(penaltyAmount));

        log.info("К сумме долга кредита {} добавлен штраф в размере {}", credit.getId(), penaltyAmount);
        creditRepository.save(credit);
    }

    private BigDecimal calculateAutoPayment(Credit credit) {
        var remainingAmount = credit.getAmountRemainingToPay();
        if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        var autoPaymentRate = credit.getTariff().getAutoPaymentRate().divide(BigDecimal.valueOf(100));
        return remainingAmount.min(credit.getAmount().multiply(autoPaymentRate));
    }

    private Optional<Credit> getCreditOptional(UUID creditId) {
        return creditRepository.findById(creditId);
    }
}
