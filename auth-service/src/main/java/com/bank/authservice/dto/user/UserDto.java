package com.bank.authservice.dto.user;

import com.bank.authservice.enumeration.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String fullName;
    private String email;
    private Gender gender;
    private Set<RoleDto> roles;
}
