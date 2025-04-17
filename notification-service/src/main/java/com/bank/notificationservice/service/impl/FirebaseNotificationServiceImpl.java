package com.bank.notificationservice.service.impl;

import com.bank.notificationservice.dto.Notification;
import com.bank.notificationservice.dto.token.RegisterTokenRequest;
import com.bank.notificationservice.dto.token.TokenDto;
import com.bank.notificationservice.entity.DeviceToken;
import com.bank.notificationservice.enumeration.Role;
import com.bank.notificationservice.exception.BadRequestException;
import com.bank.notificationservice.exception.firebase.FirebaseErrorHandler;
import com.bank.notificationservice.mapper.DeviceTokenMapper;
import com.bank.notificationservice.repository.DeviceTokenRepository;
import com.bank.notificationservice.security.CurrentUser;
import com.bank.notificationservice.service.NotificationService;
import com.bank.notificationservice.util.NotificationBuilder;
import com.bank.notificationservice.validator.RoleValidator;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseNotificationServiceImpl implements NotificationService {
    private static final int MAX_TOKENS = 500;

    private final DeviceTokenRepository deviceTokenRepository;
    private final DeviceTokenMapper deviceTokenMapper;
    private final TopicSubscriptionService topicSubscriptionService;
    private final FirebaseMessaging firebaseMessaging;
    private final FirebaseErrorHandler firebaseErrorHandler;


    @Override
    @Transactional
    public TokenDto registerToken(CurrentUser user, RegisterTokenRequest registerTokenRequest) {
        if (!RoleValidator.isReceivedRoleMatchesUserRole(user.getRoles(), registerTokenRequest.getUserRoleOnDevice())) {
            throw new BadRequestException("Роль пользователя не совпадает с ролью на устройстве");
        }

        UUID userId = user.getId();
        DeviceToken deviceToken = deviceTokenRepository.findByToken(registerTokenRequest.getToken())
                .map(token -> {
                    deviceTokenMapper.updateDeviceTokenFromRequest(token, registerTokenRequest, userId);
                    return token;
                })
                .orElseGet(() -> deviceTokenMapper.toDeviceToken(registerTokenRequest, userId));

        DeviceToken savedToken = deviceTokenRepository.save(deviceToken);

        Optional<String> tokenToRemove = topicSubscriptionService.subscribeToTopics(
                deviceToken.getUserRoleOnDevice(),
                deviceToken.getToken()
        );

        tokenToRemove.ifPresent(token -> {
            deviceTokenRepository.deleteByToken(token);
            throw new BadRequestException("Токен не зарегистрирован в Firebase");
        });

        return deviceTokenMapper.toTokenDto(savedToken);
    }

    @Override
    public void sendNotificationToTopic(String topic, Notification notification) {
        try {
            Message message = NotificationBuilder.buildMessageForTopic(topic, notification);

            String messageId = firebaseMessaging.send(message);

            log.info("В топик %s отправлено уведомление с ID %s".formatted(topic, messageId));
        } catch (FirebaseMessagingException e) {
            throw firebaseErrorHandler.handleMessagingException(e);
        }
    }

    @Override
    @Transactional
    public void sendNotificationToUserInRoles(UUID userId, List<Role> roles, Notification notification) {
        try {
            List<String> userRelevantTokens = deviceTokenRepository.findAllTokensByUserIdAndRoles(userId, roles);

            if (userRelevantTokens.size() > MAX_TOKENS) {
                log.warn("Количество токенов превышает максимально допустимое значение - %d".formatted(MAX_TOKENS));
                userRelevantTokens = userRelevantTokens.subList(0, MAX_TOKENS);
            }

            MulticastMessage message = NotificationBuilder.buildMulticastMessage(userRelevantTokens, notification);

            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);

            handleMulticastResponse(response, userRelevantTokens);

        } catch (FirebaseMessagingException e) {
            throw firebaseErrorHandler.handleMessagingException(e);
        }
    }


    private void handleMulticastResponse(BatchResponse response, List<String> tokens) {
        if (response.getFailureCount() > 0) {
            List<SendResponse> responses = response.getResponses();
            List<String> invalidTokens = new ArrayList<>();

            for (int i = 0; i < responses.size(); i++) {
                SendResponse sendResponse = responses.get(i);
                FirebaseMessagingException fme = sendResponse.getException();
                if (!sendResponse.isSuccessful() && fme != null &&
                        sendResponse.getException().getMessagingErrorCode() != null) {
                    MessagingErrorCode error = fme.getMessagingErrorCode();

                    switch (error) {
                        case UNREGISTERED, INVALID_ARGUMENT, SENDER_ID_MISMATCH -> {
                            String token = tokens.get(i);
                            invalidTokens.add(token);
                        }
                        default -> log.warn("Неизвестная ошибка при отправке уведомления: %s".formatted(
                                sendResponse.getException().getMessage()));
                    }
                }
            }

            Optional.of(invalidTokens)
                    .ifPresent(tokensToRemove -> {
                        int deletedNumber = deviceTokenRepository.deleteByTokens(tokensToRemove);
                        log.info("Удалено %d невалидных токенов".formatted(deletedNumber));
                    });
        }
    }
}
