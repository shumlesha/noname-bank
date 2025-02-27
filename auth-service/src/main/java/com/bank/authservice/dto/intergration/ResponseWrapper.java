package com.bank.authservice.dto.intergration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "responseType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ErrorResponse.class, name = "error"),
        @JsonSubTypes.Type(value = SuccessResponse.class, name = "success")
})
public interface ResponseWrapper<T> {
    T getOrThrow();
}
