package com.bank.userservice.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;
}
