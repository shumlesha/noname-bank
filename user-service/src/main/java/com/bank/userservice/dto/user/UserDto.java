package com.bank.userservice.dto.user;

import com.bank.userservice.enumeration.Gender;
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
    private boolean banned;
}
