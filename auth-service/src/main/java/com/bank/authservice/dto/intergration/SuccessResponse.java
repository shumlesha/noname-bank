package com.bank.authservice.dto.intergration;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

@JsonTypeName("success")
@Data
public class SuccessResponse<T> implements ResponseWrapper<T> {
    private T data;

    @Override
    public T getOrThrow() {
        return data;
    }
}
