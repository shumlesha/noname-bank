package ru.patterns.credit.shared.response.credit.pay;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PayCreditResponse.class, name = "success"),
        @JsonSubTypes.Type(value = PayCreditErrorResponse.class, name = "error"),
})
public interface PayCreditResponseRaw {
}
