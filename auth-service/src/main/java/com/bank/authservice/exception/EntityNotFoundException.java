package com.bank.authservice.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityType, Object id) {
        super(
                String.format("%s with id %s not found", entityType, id),
                HttpStatus.NOT_FOUND,
                Map.of(
                        "entityType", entityType,
                        "entityId", id
                )
        );
    }
}
