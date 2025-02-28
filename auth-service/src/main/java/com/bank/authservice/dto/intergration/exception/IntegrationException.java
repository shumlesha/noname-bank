package com.bank.authservice.dto.intergration.exception;

import com.bank.authservice.exception.SystemException;
import org.springframework.http.HttpStatus;

public class IntegrationException extends SystemException {
    public IntegrationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
