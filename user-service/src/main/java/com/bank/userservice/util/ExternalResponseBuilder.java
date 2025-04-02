package com.bank.userservice.util;

import com.bank.userservice.dto.api.ErrorResponse;
import com.bank.userservice.dto.api.interagration.ErrorExternalResponse;
import com.bank.userservice.dto.api.interagration.ExternalResponse;
import com.bank.userservice.dto.api.interagration.SuccessExternalResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@UtilityClass
public class ExternalResponseBuilder {
    public <T> ExternalResponse<T> success(T data) {
        return SuccessExternalResponse.<T>builder()
                .data(data)
                .build();
    }

    public static ErrorExternalResponse error(HttpStatus status, String message) {
        return ErrorExternalResponse.builder()
                .status(status)
                .message(message)
                .build();
    }

    public static ErrorResponse error(HttpStatus status, String message, Map<String, List<String>> errors) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .errors(errors)
                .build();
    }
}
