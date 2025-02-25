package com.bank.authservice.dto.intergration.exception;

import com.bank.authservice.dto.intergration.ErrorResponse;
import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public ExternalServiceException(String message, ErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }
}
