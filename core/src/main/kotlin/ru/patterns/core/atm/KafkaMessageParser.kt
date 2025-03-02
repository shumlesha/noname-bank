package ru.patterns.core.atm

import com.fasterxml.jackson.databind.ObjectMapper
import ru.patterns.core.atm.MessageParser.ParseResult

interface MessageParser<T> {
    fun parse(rawMessage: String): ParseResult<T>

    sealed interface ParseResult<T> {
        data class Success<T>(val data: T) : ParseResult<T>
        data class Error<T>(val cause: Throwable) : ParseResult<T>
    }
}

abstract class KafkaMessageParser<T>(
    private val clazz: Class<T>,
    private val objectMapper: ObjectMapper
) : MessageParser<T> {
    override fun parse(rawMessage: String): ParseResult<T> =
        try {
            objectMapper.readValue(rawMessage, clazz)
                .let { data -> ParseResult.Success(data) }
        } catch (e: Exception) {
            ParseResult.Error(e)
        }
}