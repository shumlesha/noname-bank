package ru.patterns.credit.shared.dto;

import java.math.BigDecimal;

public record PaymentResult(
        String status,
        BigDecimal debt
) {
}
