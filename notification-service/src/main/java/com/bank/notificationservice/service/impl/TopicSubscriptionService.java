package com.bank.notificationservice.service.impl;

import com.bank.notificationservice.config.firebase.FirebaseNotificationProperties;
import com.bank.notificationservice.enumeration.Role;
import com.bank.notificationservice.exception.firebase.FirebaseErrorHandler;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TopicSubscriptionService {
    private final FirebaseMessaging firebaseMessaging;
    private final FirebaseErrorHandler firebaseErrorHandler;
    private final FirebaseNotificationProperties firebaseNotificationProperties;

    @Transactional
    public Optional<String> subscribeToTopics(Role role, String token) {
        try {
            List<String> relevantTopics = firebaseNotificationProperties.getTopicMappings().getTopicsForRole(role);

            for (String topic : relevantTopics) {
                TopicManagementResponse response = firebaseMessaging.subscribeToTopic(List.of(token), topic);
                Optional<String> unregisteredToken = handleSubscriptionResponse(response, token);

                if (unregisteredToken.isPresent()) {
                    return unregisteredToken;
                }
            }

            return Optional.empty();
        } catch (FirebaseMessagingException e) {
            throw firebaseErrorHandler.handleMessagingException(e);
        }
    }

    private Optional<String> handleSubscriptionResponse(TopicManagementResponse response, String token) {
        if (response.getFailureCount() > 0) {
            TopicManagementResponse.Error firstError = response.getErrors().getFirst();

            if ("registration-token-not-registered".equals(firstError.getReason())) {
                return Optional.of(token);
            } else {
                throw firebaseErrorHandler.handleTopicError(firstError);
            }
        }

        return Optional.empty();
    }
}
