package com.bank.notificationservice.util;

import com.bank.notificationservice.dto.Notification;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushFcmOptions;
import com.google.firebase.messaging.WebpushNotification;
import lombok.experimental.UtilityClass;
import java.util.List;

@UtilityClass
public class NotificationBuilder {

    public Message buildMessageForTopic(String topic, Notification notification) {
        return Message.builder()
                .setTopic(topic)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getBody())
                        .setImage(notification.getImageURL())
                        .build()
                )
                .putAllData(notification.getData())
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(notification.getTitle())
                                .setBody(notification.getBody())
                                .setIcon(notification.getIcon())
                                .setImage(notification.getImageURL())
                                .build())
                        .setFcmOptions(WebpushFcmOptions.builder()
                                .setLink(notification.getClickAction())
                                .build())
                        .putHeader("TTL", String.valueOf(notification.getTtlInSeconds()))
                        .putAllData(notification.getData())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(notification.getTtlInSeconds())
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setTitle(notification.getTitle())
                                .setBody(notification.getBody())
                                .setIcon(notification.getIcon())
                                .setSound("default")
                                .setClickAction(notification.getClickAction())
                                .build())
                        .build())
                .build();
    }

    public MulticastMessage buildMulticastMessage(List<String> token, Notification notification) {
        return MulticastMessage.builder()
                .addAllTokens(token)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getBody())
                        .setImage(notification.getImageURL())
                        .build()
                )
                .putAllData(notification.getData())
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(notification.getTitle())
                                .setBody(notification.getBody())
                                .setIcon(notification.getIcon())
                                .setImage(notification.getImageURL())
                                .build())
                        .setFcmOptions(WebpushFcmOptions.builder()
                                .setLink(notification.getClickAction())
                                .build())
                        .putHeader("TTL", String.valueOf(notification.getTtlInSeconds()))
                        .putAllData(notification.getData())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(notification.getTtlInSeconds())
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setTitle(notification.getTitle())
                                .setBody(notification.getBody())
                                .setIcon(notification.getIcon())
                                .setSound("default")
                                .setClickAction(notification.getClickAction())
                                .build())
                        .build())
                .build();
    }
}
