package ru.patterns.core.credit

import com.fasterxml.jackson.databind.ObjectMapper

abstract class MqMessageParser<COMMAND>(
    private val objectMapper: ObjectMapper,
    private val clazz: Class<COMMAND>
) {
    fun parse(body: ByteArray): COMMAND =
        try {
            objectMapper.readValue(body, clazz)
        } catch (e: Exception) {
            throw IllegalArgumentException("Не удалось распарсить сообщение из MQ")
        }
}