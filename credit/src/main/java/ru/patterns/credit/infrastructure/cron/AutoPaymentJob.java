package ru.patterns.credit.infrastructure.cron;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.AutoPaymentCommand;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.handler.command.AutoPaymentCommandHandler;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AutoPaymentJob implements Job {

    private final CreditRepository creditRepository;
    private final AutoPaymentCommandHandler autoPaymentCommandHandler;

    @Override
    public void execute(JobExecutionContext context) {
        var creditsToPay = creditRepository.findCreditsDueForPayment(LocalDate.now());

        for (var credit : creditsToPay) {
            var remainingAmount = credit.getAmountRemainingToPay();
            var command = new AutoPaymentCommand(credit.getId(), remainingAmount);
            autoPaymentCommandHandler.handle(command);
        }
    }
}
