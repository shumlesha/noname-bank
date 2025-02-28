package com.bank.userservice.dto.event.payload;

import com.bank.userservice.dto.event.Payload;
import com.bank.userservice.enumeration.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatePayload implements Payload {
    private String email;
    private String fullName;
    private Gender gender;
    private List<String> roles;
}
