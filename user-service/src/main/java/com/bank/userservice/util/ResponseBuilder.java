package com.bank.userservice.util;


import com.bank.userservice.dto.api.DefaultResponse;
import com.bank.userservice.dto.api.ErrorApiResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.Map;

@UtilityClass
public class ResponseBuilder {
    public <T> DefaultResponse<T> success(String message, T data) {
        return DefaultResponse.<T>builder()
                .status(HttpStatus.OK)
                .message(message)
                .data(data)
                .build();
    }

    public <T> DefaultResponse<T> success(String message) {
        return success(message, null);
    }

    public ErrorApiResponse error(String message, HttpStatus status, Map<String, Object> errors) {
        return ErrorApiResponse.builder()
                .status(status)
                .message(message)
                .errors(errors)
                .build();
    }

    public ErrorApiResponse error(String message, HttpStatus status) {
        return error(message, status, null);
    }
}
