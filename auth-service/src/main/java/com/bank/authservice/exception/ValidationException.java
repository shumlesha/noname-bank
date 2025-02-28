package com.bank.authservice.exception;

public class ValidationException extends BusinessException{
    public ValidationException(String message) {
        super(message);
    }
}
