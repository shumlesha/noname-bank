package ru.patterns.interfacecontrol.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.interfacecontrol.model.HideAccountDto;
import ru.patterns.interfacecontrol.model.UpdateThemeDto;
import ru.patterns.interfacecontrol.model.UserSettingsDto;
import ru.patterns.interfacecontrol.model.common.DefaultResponse;
import ru.patterns.interfacecontrol.service.SettingsService;

import java.util.UUID;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping("/{clientId}")
    public ResponseEntity<DefaultResponse<UserSettingsDto>> getSettings(@PathVariable UUID clientId) {
        var settings = settingsService.getUserSettings(clientId);
        return ResponseEntity.ok(DefaultResponse.success(settings));
    }

    @PutMapping("/theme")
    public ResponseEntity<DefaultResponse<UserSettingsDto>> updateTheme(@RequestBody UpdateThemeDto dto) {
        var updatedSettings = settingsService.updateTheme(dto.clientId(), dto.theme());
        return ResponseEntity.ok(DefaultResponse.success(updatedSettings));
    }

    @PostMapping("/hide-account")
    public ResponseEntity<DefaultResponse<UserSettingsDto>> hideAccount(@RequestBody HideAccountDto dto) {
        var updatedSettings = settingsService.hideAccount(dto.clientId(), dto.clientId());
        return ResponseEntity.ok(DefaultResponse.success(updatedSettings));
    }

    @PostMapping("/unhide-account")
    public ResponseEntity<DefaultResponse<UserSettingsDto>> unhideAccount(@RequestBody HideAccountDto dto) {
        var updatedSettings = settingsService.unhideAccount(dto.clientId(), dto.accountId());
        return ResponseEntity.ok(DefaultResponse.success(updatedSettings));
    }
}
