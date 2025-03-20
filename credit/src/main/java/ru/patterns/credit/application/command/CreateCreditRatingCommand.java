package ru.patterns.credit.application.command;

import java.util.UUID;

public record CreateCreditRatingCommand(UUID clientId) {
}
