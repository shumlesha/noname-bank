package ru.patterns.credit.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCreditCommand(BigDecimal amount, UUID tariffId) { }
