package ru.patterns.credit.shared;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.patterns.credit.shared.response.CreateCreditAccountResponse;
import ru.patterns.credit.shared.response.ErrorResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateCreditAccountResponse.class, name = "success"),
        @JsonSubTypes.Type(value = ErrorResponse.class, name = "error"),
})
public interface CreateCreditAccountResponseRaw { }
