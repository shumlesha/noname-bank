package ru.patterns.credit.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record AutoPaymentCommand(UUID creditId, BigDecimal amount) {
}
