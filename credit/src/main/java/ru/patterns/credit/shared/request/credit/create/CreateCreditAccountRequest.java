package ru.patterns.credit.shared.request.credit.create;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateCreditAccountRequest(
        UUID clientId,
        BigDecimal amount
) implements Serializable { }
