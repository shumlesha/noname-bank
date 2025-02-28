package com.bank.userservice.util;

import lombok.experimental.UtilityClass;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ErrorUtil {
    public Map<String, Object> getMetadata(Map<String, List<String>> errors) {
        return errors != null
                ? new HashMap<>(errors)
                : new HashMap<>();
    }
}