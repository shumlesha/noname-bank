package ru.patterns.credit.application.query;

import java.util.UUID;

public record GetCreditByIdQuery(UUID creditId) { }