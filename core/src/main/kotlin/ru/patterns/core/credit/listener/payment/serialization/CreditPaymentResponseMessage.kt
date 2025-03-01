package ru.patterns.core.credit.listener.payment.serialization

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreditPaymentResponseMessage::class, name = "success"),
    JsonSubTypes.Type(value = CreditPaymentErrorMessage::class, name = "error")
)
sealed interface CreditPaymentResponse

data class CreditPaymentResponseMessage(
    val debt: BigDecimal
) : CreditPaymentResponse

data class CreditPaymentErrorMessage(
    val message: String
) : CreditPaymentResponse