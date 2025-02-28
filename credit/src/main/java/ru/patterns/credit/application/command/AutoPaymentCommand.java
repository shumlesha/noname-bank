package ru.patterns.credit.application.command;

import java.util.UUID;

public record AutoPaymentCommand(UUID creditId) {
}
