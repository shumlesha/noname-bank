package ru.patterns.credit.shared.response.credit.create;

public record ErrorResponse(
        String message
) implements CreateCreditAccountResponseRaw {
}
