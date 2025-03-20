package ru.patterns.gateway.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final KeycloakProperties keycloakProperties;

    private static final String[] PUBLIC_ROUTES = {
            "/api/auth/**",
            "/client/login", "/client/logout", "/client/callback",
            "/employee/login", "/employee/logout", "/employee/callback",
            "/actuator/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_ROUTES).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(((exchange, ex) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }))
                        .accessDeniedHandler(((exchange, ex) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }))
                ).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Total-Count"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

            String clientId = (String) jwt.getClaims().getOrDefault("azp", "app-client");

            Collection<String> resourceRoles = extractResourceRoles(resourceAccess, clientId);
            Collection<String> realmRoles = extractRealmRoles(jwt);

            Set<String> allRoles = Stream.concat(
                    resourceRoles.stream(),
                    realmRoles.stream()
            ).collect(Collectors.toSet());

            return allRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toSet());
        });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @SuppressWarnings("unchecked")
    private Collection<String> extractResourceRoles(Map<String, Object> resourceAccess, String clientId) {
        if (resourceAccess == null || !resourceAccess.containsKey(clientId)) {
            return Collections.emptySet();
        }

        Map<String, Object> clientResource = (Map<String, Object>) resourceAccess.get(clientId);
        if (clientResource == null || !clientResource.containsKey("roles")) {
            return Collections.emptySet();
        }

        Collection<String> roles = (Collection<String>) clientResource.get("roles");
        return roles != null ? roles : Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    private Collection<String> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return Collections.emptySet();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        return roles != null ? roles : Collections.emptySet();
    }
}
