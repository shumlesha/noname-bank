package ru.patterns.credit.shared.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.patterns.credit.shared.CreateCreditAccountResponseRaw;

public record CreateCreditAccountResponse(
        @JsonProperty("account") Account account
) implements CreateCreditAccountResponseRaw {
}
