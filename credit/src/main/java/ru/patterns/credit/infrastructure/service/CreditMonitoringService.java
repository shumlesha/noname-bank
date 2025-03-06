package ru.patterns.credit.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.patterns.credit.domain.model.CreditStatus;
import ru.patterns.credit.domain.repository.CreditRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditMonitoringService {
    private final CreditRepository creditRepository;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void monitorOverdueCredits() {
        var overdueCredits = creditRepository.findOverdueCredits();
        log.info("Найдено {} просроченных кредитов", overdueCredits.size());

        var today = LocalDate.now();
        var shortOverdue = 0;
        var mediumOverdue = 0;
        var longOverdue = 0;
        
        for (var credit : overdueCredits) {
            var daysOverdue = ChronoUnit.DAYS.between(credit.getNextPaymentDate(), today);
            
            if (daysOverdue <= 7) {
                shortOverdue++;
            } else if (daysOverdue <= 30) {
                mediumOverdue++;
            } else {
                longOverdue++;
            }
        }
        
        log.info("Статистика просроченных кредитов: до 7 дней: {}, от 7 до 30 дней: {}, более 30 дней: {}", 
                shortOverdue, mediumOverdue, longOverdue);
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void monitorUpcomingPayments() {
        var today = LocalDate.now();
        var upcomingDate = today.plusDays(3);
        
        var activeCredits = creditRepository.findByStatus(CreditStatus.ACTIVE);
        var upcomingPayments = activeCredits.stream()
                .filter(credit -> !credit.getNextPaymentDate().isAfter(upcomingDate) && 
                                  !credit.getNextPaymentDate().isBefore(today))
                .toList();
        
        log.info("Найдено {} кредитов с предстоящими платежами в ближайшие 3 дня", upcomingPayments.size());
    }
}
