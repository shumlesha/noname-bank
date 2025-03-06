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
import ru.patterns.credit.infrastructure.messaging.publisher.CreditPayEventPublisher;
import ru.patterns.credit.shared.exception.InsufficientFundsException;
import ru.patterns.credit.shared.exception.InternalServerException;
import ru.patterns.credit.shared.exception.PaymentProcessingException;
import ru.patterns.credit.shared.exception.ResourceNotFoundException;
import ru.patterns.credit.shared.request.credit.pay.PayCreditRequest;
import ru.patterns.credit.shared.response.credit.pay.PayCreditErrorResponse;
import ru.patterns.credit.shared.response.credit.pay.PayCreditResponse;
import ru.patterns.credit.shared.response.credit.pay.PayCreditResponseRaw;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditPaymentService {
    private final CreditRepository creditRepository;
    private final CreditPayEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public boolean processPayment(Credit credit, BigDecimal amount) {
        try {
            var payCreditRequest = new PayCreditRequest(credit.getAccountId(), amount);
            var response = eventPublisher.publishPaymentRequest(payCreditRequest);

            return handlePaymentResponse(response, credit, amount);
        } catch (InsufficientFundsException e) {
            log.error("Недостаточно средств для оплаты кредита {}: {}", credit.getId(), e.getMessage());
            markCreditAsOverdue(credit);
            return false;
        } catch (JsonProcessingException e) {
            throw new InternalServerException("Ошибка обработки ответа платежа", e);
        } catch (Exception e) {
            throw new InternalServerException("Не удалось обработать платеж", e);
        }
    }

    private boolean handlePaymentResponse(Message response, Credit credit, BigDecimal amount)
            throws IOException {

        if (response == null) {
            throw new PaymentProcessingException("Ответ с подтверждением оплаты не получен");
        }

        var responseString = new String(response.getBody(), StandardCharsets.UTF_8);
        log.info("Ответ от оплаты для кредита {}: {}", credit.getId(), responseString);
        
        var payCreditResponse = objectMapper.readValue(response.getBody(), PayCreditResponseRaw.class);

        if (payCreditResponse instanceof PayCreditResponse creditResponse) {
           if (creditResponse.debt().compareTo(BigDecimal.ZERO) == 0) {
               processSuccessfulPayment(credit, amount);
               return true;
           } else {
               throw new InsufficientFundsException("Недостаточно средств для оплаты кредита");
           }
        } else {
            throw new PaymentProcessingException("Ошибка платежа");
        }
    }
    
    private void processSuccessfulPayment(Credit credit, BigDecimal amount) {
        credit.addPayment(amount);
        credit.setNextPaymentDate(LocalDate.now().plusDays(1));
        
        if (credit.getStatus() == CreditStatus.OVERDUE) {
            credit.setStatus(CreditStatus.ACTIVE);
        }
        
        if (credit.isPaidOff()) {
            credit.setStatus(CreditStatus.PAID_OFF);
            log.info("Кредит {} полностью погашен", credit.getId());
        }
        
        creditRepository.save(credit);
    }
    
    private void markCreditAsOverdue(Credit credit) {
        if (credit.getStatus() != CreditStatus.OVERDUE) {
            credit.setStatus(CreditStatus.OVERDUE);
            creditRepository.save(credit);
        }
    }
}
