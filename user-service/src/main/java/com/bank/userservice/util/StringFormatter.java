package com.bank.userservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringFormatter {
    public String formatToHandlerName(String eventType) {
        return eventType.toLowerCase().replace("_", "");
    }
}
