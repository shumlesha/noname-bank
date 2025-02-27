package com.bank.userservice.dto.api.interagration;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;

@JsonTypeName("error")
@Data
@Builder
public class ErrorExternalResponse implements ExternalResponse<Object> {
    private HttpStatus status;
    private String message;
    private Map<String, List<String>> errors;
}
