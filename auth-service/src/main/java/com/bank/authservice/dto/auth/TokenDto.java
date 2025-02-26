package com.bank.authservice.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private UUID userId;
    private String accessToken;
    private String refreshToken;
}
