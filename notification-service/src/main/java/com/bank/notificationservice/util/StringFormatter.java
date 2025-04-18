package com.bank.notificationservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringFormatter {
    public String formatToHandlerName(String eventType) {
        return eventType.toLowerCase().replace("_", "");
    }
}
