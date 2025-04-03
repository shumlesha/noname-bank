package ru.patterns.corequery.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val keycloakJwtAuthenticationConverter: KeycloakJwtAuthenticationConverter
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .requestCache { customizer -> customizer.requestCache(NoOpServerRequestCache.getInstance()) }
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .authorizeExchange { auth ->
                auth
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter) }
            }
            .build()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfig = CorsConfiguration()
        corsConfig.addAllowedOrigin("*")
        corsConfig.addAllowedHeader("*")
        corsConfig.addAllowedMethod("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)
        return source
    }
}