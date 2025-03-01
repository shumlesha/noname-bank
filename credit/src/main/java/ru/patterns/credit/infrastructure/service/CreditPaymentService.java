package ru.patterns.credit.infrastructure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.model.CreditStatus;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditPayEventPublisher;
import ru.patterns.credit.shared.request.credit.pay.PayCreditRequest;
import ru.patterns.credit.shared.response.credit.create.CreateCreditAccountResponseRaw;
import ru.patterns.credit.shared.response.credit.pay.PayCreditResponse;
import ru.patterns.credit.shared.response.credit.pay.PayCreditResponseRaw;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditPaymentService {
    private final CreditRepository creditRepository;
    private final CreditPayEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void processPayment(UUID creditId, BigDecimal amount) {
        var credit = getCredit(creditId);

        try {
            var payCreditRequest = new PayCreditRequest(credit.getAccountId(), amount);
            var response = eventPublisher.publishPaymentRequest(payCreditRequest);

            var payCreditResponse = objectMapper.readValue(response.getBody(), PayCreditResponseRaw.class);
            if (payCreditResponse instanceof PayCreditResponse) {
                credit.addPayment(amount);
                credit.setNextPaymentDate(LocalDate.now().plusDays(1));
                creditRepository.save(credit);

                if (credit.isPaidOff()) {
                    credit.setStatus(CreditStatus.PAID_OFF);
                    creditRepository.save(credit);
                }
            } else {
                throw new IllegalStateException("Ошибка платежа");
            }

        } catch (JsonProcessingException e) {
            log.error("Ошибка при разборе ответа платежного запроса", e);
            throw new RuntimeException("Ошибка обработки ответа платежа", e);
        } catch (Exception e) {
            log.error("Ошибка при обработке платежа", e);
            throw new RuntimeException("Не удалось обработать платеж", e);
        }
    }

    public BigDecimal calculateAutoPayment(Credit credit) {
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

    private Credit getCredit(UUID creditId) {
        return creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));
    }
}

