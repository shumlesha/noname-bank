package ru.patterns.corequery.security.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.security.CurrentUser
import java.util.Locale
import java.util.UUID
import java.util.stream.Collectors
import java.util.stream.Stream


@Component
class KeycloakJwtAuthenticationConverter : Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    private val defaultGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): Mono<AbstractAuthenticationToken> {
        val defaultAuthorities = defaultGrantedAuthoritiesConverter.convert(jwt)!!
        val authorities: Collection<GrantedAuthority> = Stream.concat(
            defaultAuthorities.stream(),
            extractKeycloakAuthorities(jwt).stream()
        ).collect(Collectors.toSet())

        val userId = UUID.fromString(jwt.subject)
        val email = jwt.getClaimAsString("preferred_username")
        val attributes = jwt.claims

        val currentUser = CurrentUser(userId, email, attributes, authorities)

        val accessToken = OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            jwt.tokenValue,
            jwt.issuedAt,
            jwt.expiresAt
        )

        return BearerTokenAuthentication(currentUser, accessToken, authorities).toMono()
    }


    private fun extractKeycloakAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
        val authorities: MutableSet<GrantedAuthority> = HashSet()

        val realmAccess = jwt.getClaim<Map<String, Any>>("realm_access")
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            authorities.addAll(
                (realmAccess["roles"] as List<String>)
                    .map { role -> "ROLE_" + role.uppercase(Locale.getDefault()) }
                    .map { role -> SimpleGrantedAuthority(role) }
                    .toSet())
        }

        return authorities
    }
}