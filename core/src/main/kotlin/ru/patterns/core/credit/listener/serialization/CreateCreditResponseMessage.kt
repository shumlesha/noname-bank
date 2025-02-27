package ru.patterns.core.credit.listener.serialization

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import ru.patterns.core.domain.Account

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateCreditResponseMessage::class, name = "success"),
    JsonSubTypes.Type(value = CreateCreditErrorResponse::class, name = "error")
)
sealed interface CreateCreditMqResponse

data class CreateCreditResponseMessage(
    val account: Account
) : CreateCreditMqResponse

data class CreateCreditErrorResponse(
    val message: String
) : CreateCreditMqResponse