package ru.patterns.credit.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateCreditTariffCommand(
        UUID tariffId,
        String name,
        BigDecimal interestRate,
        BigDecimal autoPaymentRate,
        BigDecimal penaltyRate
) { }

