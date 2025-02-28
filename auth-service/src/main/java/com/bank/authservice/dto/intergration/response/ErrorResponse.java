package com.bank.authservice.dto.intergration.response;

import com.bank.authservice.dto.intergration.exception.ExternalServiceException;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;

@JsonTypeName("error")
@Data
public class ErrorResponse implements ResponseWrapper<Object> {
    private HttpStatus status;
    private String message;
    private Map<String, List<String>> errors;


    @Override
    public Object getOrThrow() {
        throw new ExternalServiceException(this);
    }
}
