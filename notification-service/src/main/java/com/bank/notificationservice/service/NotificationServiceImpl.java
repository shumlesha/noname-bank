package com.bank.notificationservice.service;

import com.bank.notificationservice.dto.token.RegisterTokenRequest;
import com.bank.notificationservice.dto.token.TokenDto;
import com.bank.notificationservice.entity.DeviceToken;
import com.bank.notificationservice.mapper.DeviceTokenMapper;
import com.bank.notificationservice.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final DeviceTokenRepository deviceTokenRepository;
    private final DeviceTokenMapper deviceTokenMapper;

    @Override
    @Transactional
    public TokenDto registerToken(UUID userId, RegisterTokenRequest registerTokenRequest) {
        DeviceToken deviceToken = deviceTokenRepository.findByToken(registerTokenRequest.getToken())
                .map(token -> {
                    deviceTokenMapper.updateDeviceTokenFromRequest(token, registerTokenRequest, userId);
                    return token;
                })
                .orElseGet(() -> deviceTokenMapper.toDeviceToken(registerTokenRequest, userId));

        DeviceToken savedToken = deviceTokenRepository.saveAndFlush(deviceToken);

        return deviceTokenMapper.toTokenDto(savedToken);
    }

}
