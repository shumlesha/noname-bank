package ru.patterns.credit.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record IncreaseCreditRatingCommand(
        UUID clientId,
        BigDecimal paidAmount,
        BigDecimal totalLoan
) { }
