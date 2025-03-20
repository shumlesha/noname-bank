package ru.patterns.credit.infrastructure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.domain.model.Credit;
import ru.patterns.credit.domain.model.CreditStatus;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.handler.command.CreditRatingCommandHandler;
import ru.patterns.credit.infrastructure.messaging.publisher.CreditPayEventPublisher;
import ru.patterns.credit.shared.dto.PaymentResult;
import ru.patterns.credit.shared.exception.InsufficientFundsException;
import ru.patterns.credit.shared.exception.InternalServerException;
import ru.patterns.credit.shared.exception.PaymentProcessingException;
import ru.patterns.credit.shared.request.credit.pay.PayCreditRequest;
import ru.patterns.credit.shared.response.credit.pay.PayCreditErrorResponse;
import ru.patterns.credit.shared.response.credit.pay.PayCreditResponse;
import ru.patterns.credit.shared.response.credit.pay.PayCreditResponseRaw;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditPaymentService {
    private static final int NEXT_PAYMENT_DELAY_DAYS = 1;

    private final CreditRepository creditRepository;
    private final CreditPayEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public PaymentResult processPayment(Credit credit, BigDecimal amount) {
        try {
            amount = adjustPaymentAmount(credit, amount);
            var payCreditRequest = new PayCreditRequest(credit.getAccountId(), amount);
            var response = eventPublisher.publishPaymentRequest(payCreditRequest);
            return handlePaymentResponse(response, credit, amount);
        } catch (InsufficientFundsException e) {
            throw new PaymentProcessingException("На счету недостаточно средств");
        } catch (JsonProcessingException e) {
            throw new InternalServerException("Ошибка обработки ответа платежа", e);
        } catch (Exception e) {
            throw new InternalServerException("Не удалось обработать платеж", e);
        }
    }

    private BigDecimal adjustPaymentAmount(Credit credit, BigDecimal amount) {
        return amount.min(credit.getAmountRemainingToPay());
    }

    private PaymentResult handlePaymentResponse(Message response, Credit credit, BigDecimal amount) throws IOException {
        if (response == null) {
            throw new PaymentProcessingException("Ответ с подтверждением оплаты не получен");
        }

        var responseString = new String(response.getBody(), StandardCharsets.UTF_8);
        log.info("Ответ от оплаты для кредита {}: {}", credit.getId(), responseString);

        var payCreditResponse = objectMapper.readValue(response.getBody(), PayCreditResponseRaw.class);

        if (payCreditResponse instanceof PayCreditResponse creditResponse) {
            return processResponseBasedOnDebt(creditResponse, credit, amount);
        } else if (payCreditResponse instanceof PayCreditErrorResponse creditErrorResponse) {
            throw new InsufficientFundsException(creditErrorResponse.message());
        } else  {
            throw new PaymentProcessingException("Некорректный формат ответа платежа");
        }
    }

    private PaymentResult processResponseBasedOnDebt(
            PayCreditResponse creditResponse,
            Credit credit,
            BigDecimal amount
    ) {
        if (creditResponse.debt().compareTo(BigDecimal.ZERO) == 0) {
            processSuccessfulPayment(credit, amount);
            return new PaymentResult("success", creditResponse.debt());
        }
        return new PaymentResult("error", creditResponse.debt());
    }

    private void processSuccessfulPayment(Credit credit, BigDecimal amount) {
        credit.addPayment(amount);
        credit.setNextPaymentDate(LocalDate.now().plusDays(NEXT_PAYMENT_DELAY_DAYS));

        if (credit.getStatus() == CreditStatus.OVERDUE) {
            credit.setStatus(CreditStatus.ACTIVE);
        }

        if (credit.isPaidOff()) {
            credit.setStatus(CreditStatus.PAID_OFF);
            log.info("Кредит {} полностью погашен", credit.getId());
        }

        creditRepository.save(credit);
    }

    private PaymentResult handlePaymentError(String errorMessage, Credit credit, BigDecimal amount, Exception e) {
        log.error("{} {}: {}", errorMessage, credit.getId(), e.getMessage());
        return new PaymentResult("error", amount);
    }
}
