package com.bank.notificationservice.exception.firebase;

import lombok.experimental.UtilityClass;
import java.util.Map;

@UtilityClass
public class TopicErrorMessages {
    private final Map<String, String> ERROR_MESSAGES = Map.of(
            "invalid-argument", "Недопустимый аргумент",
            "registration-token-not-registered", "Токен не зарегистрирован",
            "internal-error", "Внутренняя ошибка сервера Firebase",
            "too-many-topics", "Токен подписан на слишком много тем"
    );

    public String getMessageForReason(String reason) {
        return ERROR_MESSAGES.getOrDefault(reason, "Неизвестная ошибка");
    }
}
