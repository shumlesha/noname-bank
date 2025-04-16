package com.bank.notificationservice.dto.token;

import com.bank.notificationservice.enumeration.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private String token;
    private DeviceType deviceType;
    private UUID userId;
}
