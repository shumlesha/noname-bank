package com.bank.userservice.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public abstract class SystemException extends BaseException {
    protected SystemException(String message, HttpStatus httpStatus,
                              Map<String, Object> metadata) {
        super(message, httpStatus, metadata);
    }

    protected SystemException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }
}
