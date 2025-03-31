package ru.patterns.interfacecontrol.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.interfacecontrol.model.HideAccountDto;
import ru.patterns.interfacecontrol.model.UpdateThemeDto;
import ru.patterns.interfacecontrol.model.UserSettingsDto;
import ru.patterns.interfacecontrol.model.common.DefaultResponse;
import ru.patterns.interfacecontrol.security.CurrentUser;
import ru.patterns.interfacecontrol.service.SettingsService;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping()
    public ResponseEntity<DefaultResponse<UserSettingsDto>> getSettings(
            @AuthenticationPrincipal CurrentUser user
    ) {
        var settings = settingsService.getUserSettings(user.getId());
        return ResponseEntity.ok(DefaultResponse.success(settings));
    }

    @PostMapping("/theme")
    public ResponseEntity<DefaultResponse<UserSettingsDto>> updateTheme(
            @RequestBody UpdateThemeDto dto,
            @AuthenticationPrincipal CurrentUser user
    ) {
        var updatedSettings = settingsService.updateTheme(user.getId(), dto.theme());
        return ResponseEntity.ok(DefaultResponse.success(updatedSettings));
    }

    @PostMapping("/hide-account")
    public ResponseEntity<DefaultResponse<UserSettingsDto>> hideAccount(
            @RequestBody HideAccountDto dto,
            @AuthenticationPrincipal CurrentUser user
    ) {
        var updatedSettings = settingsService.hideAccount(user.getId(), dto.accountId());
        return ResponseEntity.ok(DefaultResponse.success(updatedSettings));
    }

    @PostMapping("/unhide-account")
    public ResponseEntity<DefaultResponse<UserSettingsDto>> unhideAccount(
            @RequestBody HideAccountDto dto,
            @AuthenticationPrincipal CurrentUser user
    ) {
        var updatedSettings = settingsService.unhideAccount(user.getId(), dto.accountId());
        return ResponseEntity.ok(DefaultResponse.success(updatedSettings));
    }
}
