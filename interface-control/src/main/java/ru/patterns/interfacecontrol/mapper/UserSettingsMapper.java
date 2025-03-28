package ru.patterns.interfacecontrol.mapper;

import org.springframework.stereotype.Component;
import ru.patterns.interfacecontrol.entity.UserSettings;
import ru.patterns.interfacecontrol.model.UserSettingsDto;

@Component
public class UserSettingsMapper {

    public UserSettingsDto toDto(UserSettings settings) {
        return new UserSettingsDto(settings.getUserId(), settings.getTheme(), settings.getHiddenAccounts());
    }
}
