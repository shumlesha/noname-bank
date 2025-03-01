package ru.patterns.credit.shared.response.credit.pay;

public record PayCreditErrorResponse(
        String message
) implements PayCreditResponseRaw {
}
