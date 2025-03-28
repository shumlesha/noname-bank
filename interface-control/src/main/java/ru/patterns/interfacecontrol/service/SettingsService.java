package ru.patterns.interfacecontrol.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.interfacecontrol.entity.UserSettings;
import ru.patterns.interfacecontrol.enums.Theme;
import ru.patterns.interfacecontrol.mapper.UserSettingsMapper;
import ru.patterns.interfacecontrol.model.UserSettingsDto;
import ru.patterns.interfacecontrol.repository.SettingsRepository;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;
    private final UserSettingsMapper userSettingsMapper;

    public UserSettingsDto getUserSettings(UUID userId) {
        return settingsRepository.findByUserId(userId)
                .map(userSettingsMapper::toDto)
                .orElseGet(() -> createDefaultSettings(userId));
    }

    @Transactional
    public UserSettingsDto updateTheme(UUID userId, Theme newTheme) {
        var settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettingsEntity(userId));

        settings.setTheme(newTheme);
        var savedSettings = settingsRepository.save(settings);
        return userSettingsMapper.toDto(savedSettings);
    }

    @Transactional
    public UserSettingsDto hideAccount(UUID userId, UUID accountId) {
        var settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettingsEntity(userId));

        settings.getHiddenAccounts().add(accountId.toString());
        var savedSettings = settingsRepository.save(settings);
        return userSettingsMapper.toDto(savedSettings);
    }

    @Transactional
    public UserSettingsDto unhideAccount(UUID userId, UUID accountId) {
        var settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettingsEntity(userId));

        settings.getHiddenAccounts().remove(accountId.toString());
        var savedSettings = settingsRepository.save(settings);
        return userSettingsMapper.toDto(savedSettings);
    }

    private UserSettingsDto createDefaultSettings(UUID userId) {
        var defaultSettings = createDefaultSettingsEntity(userId);
        var savedSettings = settingsRepository.save(defaultSettings);
        return userSettingsMapper.toDto(savedSettings);
    }

    private UserSettings createDefaultSettingsEntity(UUID userId) {
        return new UserSettings(userId, Theme.LIGHT, Set.of());
    }
}