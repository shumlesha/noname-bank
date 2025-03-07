package ru.patterns.credit.infrastructure.cron;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.AutoPaymentCommand;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.handler.command.AutoPaymentCommandHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoPaymentJob implements Job {

    private final CreditRepository creditRepository;
    private final AutoPaymentCommandHandler autoPaymentCommandHandler;

    @Override
    public void execute(JobExecutionContext context) {
        var creditsToPay = creditRepository.findCreditsDueForPayment(LocalDate.now());

        for (var credit : creditsToPay) {
            if (!credit.isPaidOff()) {
                var command = new AutoPaymentCommand(credit.getId());

                try {
                    autoPaymentCommandHandler.handle(command);
                } catch (Exception e) {
                    log.error("Ошибка при обработке автоплатежа для кредита {}: {}", credit.getId(), e.getMessage());
                }
            }
        }
    }
}

