package com.bank.userservice.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorApiResponse {
    private HttpStatus status;
    private String message;
    private Map<String, Object> errors;
}
