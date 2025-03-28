package ru.patterns.interfacecontrol.model;

import java.util.UUID;

public record HideAccountDto(
        UUID clientId,
        UUID accountId
) {
}
