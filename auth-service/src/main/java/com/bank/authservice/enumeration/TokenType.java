package com.bank.authservice.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    ACCESS("access"),
    REFRESH("refresh");

    private final String type;
}
