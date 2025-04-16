package com.bank.notificationservice.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceType {
    WEB("web"),
    ANDROID("android"),
    IOS("ios"),
    DESKTOP("desktop");

    private final String type;
}
