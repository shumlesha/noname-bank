package com.bank.notificationservice.service;

import com.bank.notificationservice.dto.Notification;
import com.bank.notificationservice.dto.token.RegisterTokenRequest;
import com.bank.notificationservice.dto.token.TokenDto;
import com.bank.notificationservice.enumeration.Role;
import com.bank.notificationservice.security.CurrentUser;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    TokenDto registerToken(CurrentUser user, RegisterTokenRequest registerTokenRequest);

    void sendNotificationToTopic(String topic, Notification notification);

    void sendNotificationToUserInRoles(UUID userId, List<Role> roles, Notification notification);
}
