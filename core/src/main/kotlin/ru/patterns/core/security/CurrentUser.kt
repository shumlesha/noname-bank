package ru.patterns.core.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import java.security.Principal
import java.util.UUID

data class CurrentUser(
    val id: UUID,
    private val email: String,
    private val attributes: Map<String, Any>,
    private val authorities: Collection<GrantedAuthority>
) : Principal, OAuth2AuthenticatedPrincipal {
    override fun getName(): String = email
    override fun getAttributes(): Map<String, Any> = attributes
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    val fullName: String = attributes["fullName"].toString()

    val isBanned: Boolean = run {
        val enabled = attributes["user_enabled"].toString()
        !enabled.toBoolean()
    }

    val roles: List<String> = authorities
        .map { obj -> obj.authority }
        .map { role -> role.substring(5) }
}