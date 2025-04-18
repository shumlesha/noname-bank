package com.bank.notificationservice.dto.api.interagration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "responseType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SuccessExternalResponse.class, name = "error"),
        @JsonSubTypes.Type(value = ErrorExternalResponse.class, name = "success")
})
public interface ExternalResponse<T> {

}
