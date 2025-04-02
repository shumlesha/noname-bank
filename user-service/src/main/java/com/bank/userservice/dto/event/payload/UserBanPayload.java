package com.bank.userservice.dto.event.payload;

import com.bank.userservice.dto.event.Payload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBanPayload implements Payload {
    private UUID userId;
    private UUID bannedBy;
    private String reason;
}
