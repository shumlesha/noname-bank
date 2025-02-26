package com.bank.authservice.security;

import com.bank.authservice.security.service.JwtTokenProvider;
import com.bank.authservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;

@AllArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtUtil jwtUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String authHeader = ((HttpServletRequest) servletRequest).getHeader(AUTHORIZATION_HEADER);

        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null && jwtTokenProvider.validateAccessToken(token)) {
            try {
                UsernamePasswordAuthenticationToken authToken = jwtUtil.getAuthentication(token);

                if (authToken != null) {
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) servletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
