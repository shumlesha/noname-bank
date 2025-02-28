package com.bank.userservice.dto.event.payload;

import com.bank.userservice.dto.event.Payload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGetPayload implements Payload {
    private String email;
}
