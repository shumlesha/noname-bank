package ru.patterns.credit.shared.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MissedPaymentDto(
        UUID id,
        UUID clientId,
        UUID creditId,
        LocalDate missedDate,
        BigDecimal debt,
        BigDecimal amount
) {
}
