package ru.patterns.credit.shared.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateCreditAccountResponse(
        UUID id,
        LocalDateTime creationTimestamp,
        LocalDateTime blockedTimestamp,
        LocalDateTime closedTimestamp,
        UUID clientId,
        String number,
        BigDecimal balance,
        boolean isCredit
) {
}
