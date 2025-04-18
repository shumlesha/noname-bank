package com.bank.notificationservice.dto.token;

import com.bank.notificationservice.enumeration.DeviceType;
import com.bank.notificationservice.enumeration.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterTokenRequest {
    @NotBlank(message = "Токен не должен быть пустым")
    private String token;

    @NotNull(message = "Тип устройства не должен быть пустым")
    private DeviceType deviceType;

    @NotNull(message = "Роль пользователя на устройстве не должна быть пустой")
    private Role userRoleOnDevice;
}
