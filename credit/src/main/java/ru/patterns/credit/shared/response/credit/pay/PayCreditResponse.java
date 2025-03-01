package ru.patterns.credit.shared.response.credit.pay;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PayCreditResponse(
        @JsonProperty("debt") BigDecimal debt
) implements PayCreditResponseRaw {
}
