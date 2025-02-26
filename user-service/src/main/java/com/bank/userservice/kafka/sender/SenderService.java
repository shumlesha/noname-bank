package com.bank.userservice.kafka.sender;

public interface SenderService<T> {
    void sendEvent(String topic, T event);
}
