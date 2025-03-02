package ru.patterns.credit.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CreditProcessingException extends RuntimeException {
    public CreditProcessingException(String message) {
        super(message);
    }
}
