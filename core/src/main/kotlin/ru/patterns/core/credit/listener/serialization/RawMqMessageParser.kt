package ru.patterns.core.credit.listener.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import ru.patterns.core.commands.account.CreateCreditAccountCommand

@Component
class RawMqMessageParser(
    private val objectMapper: ObjectMapper
) {
    fun parse(json: String): CreateCreditAccountCommand =
        try {
            objectMapper.readValue(json, CreateCreditAccountCommand::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Не удалось распарсить сообщение из MQ")
        }
}