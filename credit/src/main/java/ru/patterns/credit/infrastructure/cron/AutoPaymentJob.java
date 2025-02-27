package ru.patterns.credit.infrastructure.cron;

import lombok.RequiredArgsConstructor;
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
public class AutoPaymentJob implements Job {

    private final CreditRepository creditRepository;
    private final AutoPaymentCommandHandler autoPaymentCommandHandler;

    @Override
    public void execute(JobExecutionContext context) {
        var creditsToPay = creditRepository.findCreditsDueForPayment(LocalDate.now());

        for (var credit : creditsToPay) {
            var interestRate = credit.getTariff().getInterestRate();
            var remainingAmount = credit.getAmountRemainingToPay();

            var paymentAmount = remainingAmount.multiply(interestRate)
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

            if (paymentAmount.compareTo(BigDecimal.ZERO) > 0) {
                var command = new AutoPaymentCommand(credit.getId(), paymentAmount);
                autoPaymentCommandHandler.handle(command);
            }
        }
    }
}
