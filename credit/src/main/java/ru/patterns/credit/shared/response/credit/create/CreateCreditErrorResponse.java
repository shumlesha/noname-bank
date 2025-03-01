package ru.patterns.credit.shared.response.credit.create;

public record CreateCreditErrorResponse(
        String message
) implements CreateCreditAccountResponseRaw {
}
