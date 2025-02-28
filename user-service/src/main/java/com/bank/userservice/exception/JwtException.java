package com.bank.userservice.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtException extends AuthenticationException {
    public JwtException(String msg) {
        super(msg);
    }
}
