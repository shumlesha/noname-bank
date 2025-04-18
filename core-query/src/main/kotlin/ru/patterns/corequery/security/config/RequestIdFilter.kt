package ru.patterns.corequery.security.config

import org.slf4j.MDC
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

class RequestIdFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val headerValue = exchange.request.headers.getFirst("requestId")

        MDC.put("requestId", headerValue ?: UUID.randomUUID().toString())

        return chain.filter(exchange)
    }
}