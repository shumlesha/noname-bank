package com.bank.notificationservice.exception;


import com.bank.notificationservice.dto.api.ErrorApiResponse;
import com.bank.notificationservice.util.ErrorUtil;
import com.bank.notificationservice.util.ResponseBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorApiResponse> handleBusinessException(BusinessException e) {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                e.getMessage(),
                e.getHttpStatus(),
                e.getMetadata()
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorApiResponse> handleSystemException(SystemException e) {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                e.getMessage(),
                e.getHttpStatus(),
                e.getMetadata()
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorApiResponse> handleForbiddenException() {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                "Access denied",
                HttpStatus.FORBIDDEN
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorApiResponse> handleBadCredentialsException(BadCredentialsException e) {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorApiResponse> handleInternalAuthenticationServiceException(
            InternalAuthenticationServiceException e) {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorApiResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        Map<String, List<String>> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

        if (errors.isEmpty()) {
            errors = Map.of(Objects.requireNonNull(e.getBindingResult().getTarget()).getClass().getSimpleName(),
                    e.getBindingResult()
                            .getGlobalErrors()
                            .stream()
                            .map(ObjectError::getDefaultMessage)
                            .toList());
        }

        Map<String, Object> metadata = ErrorUtil.getMetadata(errors);

        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                metadata
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorApiResponse> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, List<String>> errors = e.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));

        Map<String, Object> metadata = ErrorUtil.getMetadata(errors);

        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                metadata
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorApiResponse> handleNotFoundException(NotFoundException e) {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                e.getMessage(),
                HttpStatus.NOT_FOUND
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorApiResponse> handleBadRequestException(jakarta.ws.rs.BadRequestException e) {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorApiResponse> handleForbiddenException(ForbiddenException e) {
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                e.getMessage(),
                HttpStatus.FORBIDDEN
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorApiResponse> handleException(Exception e) {
        log.info("Cannot handle");
        log.error("Exception message: {}", e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        ErrorApiResponse errorApiResponse = ResponseBuilder.error(
                "Unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR
        );

        return ResponseEntity.status(errorApiResponse.getStatus()).body(errorApiResponse);
    }
}
