package com.bank.notificationservice.exception;


public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {
        super(message);
    }
}
