package com.bank.notificationservice.service;

import com.bank.notificationservice.dto.token.RegisterTokenRequest;
import com.bank.notificationservice.dto.token.TokenDto;
import java.util.UUID;

public interface NotificationService {
    TokenDto registerToken(UUID userId, RegisterTokenRequest registerTokenRequest);
}
