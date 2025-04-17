package com.bank.notificationservice.service.impl;

import com.bank.notificationservice.dto.Notification;
import com.bank.notificationservice.dto.transaction.TransactionEntry;
import com.bank.notificationservice.enumeration.Role;
import com.bank.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionConsumerService {
    @Value("${icons.transaction.success}")
    private String transactionSuccessIcon;

    @Value("${notification.transaction.default-ttl}")
    private long defaultTtlInSeconds;

    private final NotificationService notificationService;

    @KafkaListener(topics = "TRANSACTION", containerFactory = "transactionKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransaction(@Payload TransactionEntry transaction, Acknowledgment acknowledgment) {
        log.info("Пришла транзакция: id = %s, userId = %s".formatted(transaction.getId(), transaction.getClientId()));

        try {
            Notification notification = buildTransactionNotification(transaction);

            notificationService.sendNotificationToTopic("TRANSACTION", notification);

            notificationService.sendNotificationToUserInRoles(transaction.getClientId(), List.of(Role.CLIENT), notification);

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка при обработке транзакции: %s".formatted(e.getMessage()));
            throw e;
        }
    }

    private Notification buildTransactionNotification(TransactionEntry transaction) {
        String transactionType = "";
        String notificationBody = "";

        LocalDateTime transactionTimestamp = transaction.getTransactionTimestamp();
        String formattedTime = transactionTimestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        if (transaction.getAccountFrom() == null && transaction.getAccountTo() != null) {
            transactionType = "Пополнение";
            notificationBody = """
                    Счет пополнения: %s
                    Сумма: %s
                    Время: %s
                    """.formatted(transaction.getAccountTo(), transaction.getAmount(),
                    formattedTime);
        } else if (transaction.getAccountFrom() != null && transaction.getAccountTo() == null) {
            transactionType = "Снятие";
            notificationBody = """
                    Счет снятия: %s
                    Сумма: %s
                    Время: %s
                    """.formatted(transaction.getAccountFrom(), transaction.getAmount(),
                    formattedTime);
        } else if (transaction.getAccountFrom() != null) {
            transactionType = "Перевод";
            notificationBody = """
                    Счет отправителя: %s
                    Счет получателя: %s
                    Сумма: %s
                    Время: %s
                    """.formatted(transaction.getAccountFrom(), transaction.getAccountTo(),
                    transaction.getAmount(), formattedTime);
        }

        return Notification.builder()
                .title("Новая транзакция: %s".formatted(transactionType))
                .body(notificationBody)
                .icon(transactionSuccessIcon)
                .ttlInSeconds(defaultTtlInSeconds)
                .build();
    }

}
