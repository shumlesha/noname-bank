package ru.patterns.credit.application.query;

import java.util.UUID;

public record GetMissedPaymentsByClientQuery(UUID clientId) {
}
