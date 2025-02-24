package ru.patterns.credit.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record PayCreditCommand(UUID creditId, BigDecimal amount) { }
