package ru.patterns.credit.shared.response;

import ru.patterns.credit.shared.CreateCreditAccountResponseRaw;

public record ErrorResponse(
        String message
) implements CreateCreditAccountResponseRaw {
}
