package com.bank.userservice.security;

import com.bank.userservice.exception.JwtException;
import com.bank.userservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String authHeader = ((HttpServletRequest) servletRequest).getHeader(AUTHORIZATION_HEADER);

        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null) {
            try {
                UsernamePasswordAuthenticationToken authToken = jwtUtil.getAuthentication(token);

                if (authToken != null) {
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) servletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info(authToken.getAuthorities().toString());
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                SecurityContextHolder.clearContext();
            }
        } else {
            SecurityContextHolder.clearContext();
            (servletRequest).setAttribute("jwtException", new JwtException("Missing auth token"));
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
