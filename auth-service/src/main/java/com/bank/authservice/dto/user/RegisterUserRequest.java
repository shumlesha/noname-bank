package com.bank.authservice.dto.user;

import com.bank.authservice.enumeration.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {
    private String email;
    private String fullName;
    private Gender gender;
    private String password;
}
