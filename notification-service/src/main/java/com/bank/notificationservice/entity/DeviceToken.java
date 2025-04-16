package com.bank.notificationservice.entity;

import com.bank.notificationservice.enumeration.DeviceType;
import com.bank.notificationservice.enumeration.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "device_tokens")
public class DeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 1024)
    private String token;

    @Column(nullable = false)
    private UUID userId;

    @Column
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column
    @Enumerated(EnumType.STRING)
    private Role userRoleOnDevice;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private Instant updatedAt;
}
