package com.bank.authservice.util;

import lombok.experimental.UtilityClass;
import java.util.UUID;

@UtilityClass
public class TokenUtil {
    public String generateJti() {
        return UUID.randomUUID().toString();
    }
}
