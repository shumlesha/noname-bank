package com.bank.authservice.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtUtil {
    private final UserDetailsService userDetailsService;

    public UsernamePasswordAuthenticationToken getAuthentication(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (expirationTime == null || expirationTime.before(new Date())) {
            throw new JOSEException("Token expired");
        }

        String email = signedJWT.getJWTClaimsSet().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    public static String extractJwtFromHeader(String header) {
        if (header == null ||!header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
