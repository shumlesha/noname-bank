package com.bank.userservice.dto.event;

import com.bank.userservice.dto.event.payload.UserCreatePayload;
import com.bank.userservice.dto.event.payload.UserGetPayload;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMessage<T extends Payload> {
    @Pattern(regexp = "^[A-Z_]+$")
    private String eventType;

    @NotNull
    private Instant timestamp;

    @NotNull
    @Valid
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "eventType",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UserCreatePayload.class, name = "USER_CREATE"),
            @JsonSubTypes.Type(value = UserGetPayload.class, name = "USER_GET")
    })
    private T payload;
}
