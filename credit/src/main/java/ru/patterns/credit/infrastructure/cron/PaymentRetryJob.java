package ru.patterns.credit.infrastructure.cron;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.command.AutoPaymentCommand;
import ru.patterns.credit.domain.model.CreditStatus;
import ru.patterns.credit.domain.repository.CreditRepository;
import ru.patterns.credit.infrastructure.handler.command.AutoPaymentCommandHandler;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRetryJob implements Job {

    private final CreditRepository creditRepository;
    private final AutoPaymentCommandHandler autoPaymentCommandHandler;

    @Override
    public void execute(JobExecutionContext context) {
        var overdueCredits = creditRepository.findByStatus(CreditStatus.OVERDUE);
        var today = LocalDate.now();
        var creditsToRetry = overdueCredits.stream()
                .filter(credit -> !credit.getNextPaymentDate().isAfter(today))
                .toList();

        for (var credit : creditsToRetry) {
            try {
                var command = new AutoPaymentCommand(credit.getId());
                autoPaymentCommandHandler.handle(command);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
    }
}
