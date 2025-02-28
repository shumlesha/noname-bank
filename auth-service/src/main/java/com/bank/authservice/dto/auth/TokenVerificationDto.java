package com.bank.authservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenVerificationDto {
    private boolean isVerified;
    private UUID userId;
}
