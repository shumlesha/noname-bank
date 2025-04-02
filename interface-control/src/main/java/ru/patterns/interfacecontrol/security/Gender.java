package ru.patterns.interfacecontrol.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("Мужской", "м."),
    FEMALE("Женский", "ж.");

    private final String name;
    private final String shortName;
}
