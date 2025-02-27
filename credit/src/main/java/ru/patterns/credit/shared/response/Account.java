package ru.patterns.credit.shared.response;

import ru.patterns.credit.shared.CreateCreditAccountResponseRaw;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Account(
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
