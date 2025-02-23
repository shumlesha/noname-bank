package ru.patterns.credit.shared.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCreditAccountRequest(
        UUID clientId,
        BigDecimal amount
) {
}
