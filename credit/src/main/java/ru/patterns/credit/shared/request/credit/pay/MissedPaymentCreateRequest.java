package ru.patterns.credit.shared.request.credit.pay;

import java.math.BigDecimal;

public record MissedPaymentCreateRequest(
        BigDecimal debt,
        BigDecimal amount
) {
}
