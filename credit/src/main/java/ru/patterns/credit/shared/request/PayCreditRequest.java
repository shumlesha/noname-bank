package ru.patterns.credit.shared.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PayCreditRequest(UUID accountId, BigDecimal amount) {
}
