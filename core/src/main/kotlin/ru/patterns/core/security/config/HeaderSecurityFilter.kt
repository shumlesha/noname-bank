package ru.patterns.core.security.config

import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import ru.patterns.core.config.SecurityProperties

class HeaderSecurityFilter(
    private val securityProperties: SecurityProperties
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val headerValue = exchange.request.headers.getFirst(securityProperties.header)

        return if (headerValue == securityProperties.secretKey) {
            chain.filter(exchange)
        } else {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            exchange.response.setComplete()
        }
    }
}