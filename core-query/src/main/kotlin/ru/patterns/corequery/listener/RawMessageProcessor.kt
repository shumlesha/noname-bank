package ru.patterns.corequery.listener

import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

abstract class RawMessageProcessor<T>(
    private val parser: MessageParser<T>,
    private val eventProcessor: EventProcessor<T>
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun process(rawMessage: String): Mono<Unit> =
        rawMessage
            .toMono()
            .map(parser::parse)
            .flatMap { parserResult ->
                when (parserResult) {
                    is MessageParser.ParseResult.Success -> {
                        eventProcessor.process(parserResult.data)
                    }

                    is MessageParser.ParseResult.Error -> {
                        log.error("При десериализации сообщения произошла ошибка", parserResult.cause)
                        Unit.toMono()
                    }
                }
            }
            .map { }
            .onErrorResume { Unit.toMono() }
}