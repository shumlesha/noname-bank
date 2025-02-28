package ru.patterns.credit.shared.response.credit.create;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCreditAccountResponse(
        @JsonProperty("account") Account account
) implements CreateCreditAccountResponseRaw {
}
