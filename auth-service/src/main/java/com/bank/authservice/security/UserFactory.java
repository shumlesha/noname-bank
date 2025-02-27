package com.bank.authservice.security;

import com.bank.authservice.entity.UserCredentials;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserFactory {

    public CurrentUser create(UserCredentials userCredentials) {
        return new CurrentUser(
                userCredentials.getUserId(),
                userCredentials.getEmail(),
                userCredentials.getPasswordHash()
        );
    }

}
