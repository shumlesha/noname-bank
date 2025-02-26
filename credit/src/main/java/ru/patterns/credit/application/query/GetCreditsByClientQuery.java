package ru.patterns.credit.application.query;

import java.util.UUID;

public record GetCreditsByClientQuery(UUID clientId) { }
