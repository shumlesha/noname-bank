package com.bank.notificationservice.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

public class EntityAlreadyExistsException extends BusinessException {
    public EntityAlreadyExistsException(String entityType, String field, Object id) {
        super(
                String.format("%s with %s %s already exists", entityType, field, id),
                HttpStatus.CONFLICT,
                Map.of(
                        "entityType", entityType,
                        "field", field,
                        "entityId", id
                )
        );
    }
}
