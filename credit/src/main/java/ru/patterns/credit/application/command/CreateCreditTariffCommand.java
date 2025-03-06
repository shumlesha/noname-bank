package ru.patterns.credit.application.command;

import java.math.BigDecimal;

public record CreateCreditTariffCommand(String name, BigDecimal interestRate, BigDecimal autoPaymentRate, BigDecimal penaltyRate) { }

