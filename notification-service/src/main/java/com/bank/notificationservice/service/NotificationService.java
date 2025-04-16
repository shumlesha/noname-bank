package com.bank.notificationservice.service;

import com.bank.notificationservice.dto.token.RegisterTokenRequest;
import com.bank.notificationservice.dto.token.TokenDto;
import com.bank.notificationservice.security.CurrentUser;

public interface NotificationService {
    TokenDto registerToken(CurrentUser user, RegisterTokenRequest registerTokenRequest);
}
