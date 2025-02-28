package com.bank.authservice.util;

import com.bank.authservice.dto.intergration.response.ErrorResponse;
import lombok.experimental.UtilityClass;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ErrorUtil {
    public Map<String, Object> getMetadata(ErrorResponse errorResponse) {
        return errorResponse.getErrors() != null
                ? new HashMap<>(errorResponse.getErrors())
                : new HashMap<>();
    }

    public Map<String, Object> getMetadata(Map<String, List<String>> errors) {
        return errors != null
                ? new HashMap<>(errors)
                : new HashMap<>();
    }
}