package com.bank.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private String title;
    private String body;
    private String imageURL;
    private String icon;
    private String clickAction;
    private String color;
    private String category;
    private long ttlInSeconds;
    private Map<String, String> data;
}
