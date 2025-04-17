package com.bank.notificationservice.exception.firebase;

import com.bank.notificationservice.exception.BadRequestException;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.TopicManagementResponse;
import org.springframework.stereotype.Component;

@Component
public class FirebaseErrorHandler {
    public BadRequestException handleTopicError(TopicManagementResponse.Error error) {
        return new BadRequestException(TopicErrorMessages.getMessageForReason(error.getReason()));
    }

    public BadRequestException handleMessagingException(FirebaseMessagingException exception) {
        MessagingErrorCode errorCode = exception.getMessagingErrorCode();

        return new BadRequestException(MessagingErrorMessages.getMessageForErrorCode(errorCode));
    }
}
