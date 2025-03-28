package ru.patterns.interfacecontrol.model;

import ru.patterns.interfacecontrol.enums.Theme;

import java.util.Set;
import java.util.UUID;

public record UserSettingsDto(
        UUID userId,
        Theme theme,
        Set<String> hiddenAccounts
) {
}
