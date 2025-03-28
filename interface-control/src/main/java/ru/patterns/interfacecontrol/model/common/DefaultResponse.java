package ru.patterns.interfacecontrol.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class DefaultResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private HttpStatus status;

    public static <T> DefaultResponse<T> success(T data) {
        return DefaultResponse.<T>builder()
                .success(true)
                .data(data)
                .status(HttpStatus.OK)
                .build();
    }

    public static <T> DefaultResponse<T> error(String message, HttpStatus status) {
        return DefaultResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status)
                .build();
    }
}
