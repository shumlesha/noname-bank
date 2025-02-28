package com.bank.authservice.dto.auth;

import com.bank.authservice.dto.user.Role;
import com.bank.authservice.enumeration.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {
    private String email;
    private String fullName;
    private Gender gender;
    private List<Role> roles;
    private String password;
}
