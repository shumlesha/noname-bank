package ru.patterns.credit.shared.response;

import ru.patterns.credit.shared.CreateCreditAccountResponseRaw;

public record CreateCreditResponseMessage(
        CreateCreditAccountResponse account
) implements CreateCreditAccountResponseRaw {
}
