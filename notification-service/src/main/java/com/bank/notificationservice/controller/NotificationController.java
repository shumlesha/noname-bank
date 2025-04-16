package com.bank.notificationservice.controller;

import com.bank.notificationservice.dto.api.DefaultResponse;
import com.bank.notificationservice.dto.token.RegisterTokenRequest;
import com.bank.notificationservice.dto.token.TokenDto;
import com.bank.notificationservice.security.CurrentUser;
import com.bank.notificationservice.service.NotificationService;
import com.bank.notificationservice.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/token")
    public ResponseEntity<DefaultResponse<TokenDto>> registerToken(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody RegisterTokenRequest registerTokenRequest
            ) {
        TokenDto savedToken = notificationService.registerToken(currentUser, registerTokenRequest);

        return ResponseEntity.ok(ResponseBuilder.success(
                "Токен успешно зарегистрирован",
                savedToken
        ));
    }
}
