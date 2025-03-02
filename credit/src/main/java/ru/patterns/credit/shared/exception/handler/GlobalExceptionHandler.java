package ru.patterns.credit.shared.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.patterns.credit.shared.common.DefaultResponse;
import ru.patterns.credit.shared.exception.CreditProcessingException;
import ru.patterns.credit.shared.exception.InternalServerException;
import ru.patterns.credit.shared.exception.PaymentProcessingException;
import ru.patterns.credit.shared.exception.ResourceNotFoundException;
import ru.patterns.credit.shared.exception.ValidationException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DefaultResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.error("Ошибка 404: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(DefaultResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<DefaultResponse<Void>> handleValidationError(ValidationException ex) {
        log.warn("Ошибка 400 2: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DefaultResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(CreditProcessingException.class)
    public ResponseEntity<DefaultResponse<Void>> handleCreditProcessingException(CreditProcessingException ex) {
        log.warn("Ошибка 400: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DefaultResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<DefaultResponse<Void>> handlePaymentError(PaymentProcessingException ex) {
        log.warn("Ошибка 402: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(DefaultResponse.error(ex.getMessage(), HttpStatus.PAYMENT_REQUIRED));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<DefaultResponse<Void>> handleInternalServerError(InternalServerException ex) {
        log.error("Ошибка 500: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DefaultResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultResponse<Void>> handleGenericError(Exception ex) {
        log.error("Неизвестная ошибка: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DefaultResponse.error("Произошла внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}

