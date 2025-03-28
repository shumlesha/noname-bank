package ru.patterns.interfacecontrol.model;

import ru.patterns.interfacecontrol.enums.Theme;

import java.util.UUID;

public record UpdateThemeDto(
        Theme theme,
        UUID clientId
){
}
