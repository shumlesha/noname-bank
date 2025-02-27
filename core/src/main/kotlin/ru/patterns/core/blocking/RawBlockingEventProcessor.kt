package ru.patterns.core.blocking

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.domain.ClientId
import java.util.UUID

@Component
class RawBlockingEventProcessor(
    private val objectMapper: ObjectMapper,
    private val clientBlockingEventProcessor: ClientBlockingEventProcessor
) {
    private val log = LoggerFactory.getLogger(RawBlockingEventProcessor::class.java)

    fun processRawMessage(rawMessage: String): Mono<Unit> =
        Mono.fromCallable { objectMapper.readTree(rawMessage) }
            .doOnSuccess { log.info(it.toString()) }
            .map { jsonNode -> extractClientIdFromJsonNode(jsonNode) }
            .flatMap { clientBlockingEventProcessor.blockClientAccounts(it) }
            .map { }
            .onErrorResume { Unit.toMono() }

    private fun extractClientIdFromJsonNode(jsonNode: JsonNode) =
        UUID.fromString(
            jsonNode
                .get("payload")
                .get("userId")
                .asText()
        )
            .let { ClientId(it) }
}