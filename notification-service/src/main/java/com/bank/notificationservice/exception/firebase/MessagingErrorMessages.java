package com.bank.notificationservice.exception.firebase;

import com.google.firebase.messaging.MessagingErrorCode;
import lombok.experimental.UtilityClass;
import java.util.Map;

@UtilityClass
public class MessagingErrorMessages {
    private final Map<MessagingErrorCode, String> ERROR_MESSAGES = Map.ofEntries(
            Map.entry(MessagingErrorCode.THIRD_PARTY_AUTH_ERROR, "Проблема с аутентификацией стороннего сервиса"),
            Map.entry(MessagingErrorCode.INVALID_ARGUMENT, "Недопустимый аргумент"),
            Map.entry(MessagingErrorCode.INTERNAL, "Внутренняя ошибка сервера Firebase"),
            Map.entry(MessagingErrorCode.QUOTA_EXCEEDED, "Превышена квота для токена"),
            Map.entry(MessagingErrorCode.SENDER_ID_MISMATCH, "ID отправителя токена регистрации не совпадает с ID проверенного отправителя"),
            Map.entry(MessagingErrorCode.UNAVAILABLE, "Сервер Firebase перегружен"),
            Map.entry(MessagingErrorCode.UNREGISTERED, "Приложение не зарегистрировано в FCM")
    );

    public String getMessageForErrorCode(MessagingErrorCode errorCode) {
        return ERROR_MESSAGES.getOrDefault(errorCode, "Неизвестная ошибка");
    }
}


