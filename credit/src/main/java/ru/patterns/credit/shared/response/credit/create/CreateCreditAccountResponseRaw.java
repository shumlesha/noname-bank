package ru.patterns.credit.shared.response.credit.create;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateCreditAccountResponse.class, name = "success"),
        @JsonSubTypes.Type(value = ErrorResponse.class, name = "error"),
})
public interface CreateCreditAccountResponseRaw { }
