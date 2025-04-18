package com.bank.notificationservice.dto.api.interagration;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;

@JsonTypeName("success")
@Data
@Builder
public class SuccessExternalResponse<T> implements ExternalResponse<T> {
    private T data;
}
