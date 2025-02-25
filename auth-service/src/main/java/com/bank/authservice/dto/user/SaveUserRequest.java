package com.bank.authservice.dto.user;

import com.bank.authservice.enumeration.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveUserRequest implements Serializable {
    private String email;
    private String fullName;
    private Gender gender;
}
