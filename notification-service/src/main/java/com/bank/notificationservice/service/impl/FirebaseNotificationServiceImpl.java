package com.bank.notificationservice.service.impl;

import com.bank.notificationservice.dto.token.RegisterTokenRequest;
import com.bank.notificationservice.dto.token.TokenDto;
import com.bank.notificationservice.entity.DeviceToken;
import com.bank.notificationservice.exception.BadRequestException;
import com.bank.notificationservice.mapper.DeviceTokenMapper;
import com.bank.notificationservice.repository.DeviceTokenRepository;
import com.bank.notificationservice.security.CurrentUser;
import com.bank.notificationservice.service.NotificationService;
import com.bank.notificationservice.validator.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseNotificationServiceImpl implements NotificationService {
    private final DeviceTokenRepository deviceTokenRepository;
    private final DeviceTokenMapper deviceTokenMapper;
    private final TopicSubscriptionService topicSubscriptionService;

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
}
