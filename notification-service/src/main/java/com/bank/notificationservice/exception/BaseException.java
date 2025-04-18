package com.bank.notificationservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final transient Map<String, Object> metadata;

    protected BaseException(String message, HttpStatus httpStatus,
                            Map<String, Object> metadata) {
        super(message);
        this.httpStatus = httpStatus;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}
