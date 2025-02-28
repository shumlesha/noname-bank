package com.bank.userservice.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

public abstract class BusinessException extends BaseException {
    protected BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST, null);
    }

    protected BusinessException(String message, HttpStatus httpStatus,
                                Map<String, Object> metadata) {
        super(message, httpStatus, metadata);
    }
}
