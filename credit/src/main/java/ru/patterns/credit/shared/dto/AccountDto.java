package ru.patterns.credit.shared.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDto(
        UUID id,
        String creationTimestamp,
        String blockedTimestamp,
        String closedTimestamp,
        UUID clientId,
        String number,
        BigDecimal balance,
        boolean isCredit
) { }
