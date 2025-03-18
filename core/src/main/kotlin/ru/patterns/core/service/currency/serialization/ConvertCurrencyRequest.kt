package ru.patterns.core.service.currency.serialization

import ru.patterns.core.domain.CurrencyCode
import java.math.BigDecimal

data class ConvertCurrencyRequest(
    val currencyFrom: CurrencyCode,
    val currencyTo: CurrencyCode,
    /**
     * Сумма в изначальной валюте
     */
    val amount: BigDecimal
)

data class ConvertCurrencyResponse(
    val currencyFrom: CurrencyCode,
    val currencyTo: CurrencyCode,
    /**
     * Сумма в сконвертированной валюте
     */
    val convertedAmount: BigDecimal
)

data class CurrencyWithRate(
    val currency: CurrencyCode,
    val rate: BigDecimal
)