package ru.patterns.core.service.currency


import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.patterns.core.config.CurrencyProperties
import ru.patterns.core.domain.CurrencyCode
import ru.patterns.core.service.currency.CurrencyService.ConvertCurrencyResult
import ru.patterns.core.service.currency.CurrencyService.GetCurrencyRateResult
import ru.patterns.core.service.currency.serialization.ConvertCurrencyRequest
import ru.patterns.core.service.currency.serialization.ConvertCurrencyResponse
import ru.patterns.core.service.currency.serialization.CurrencyList
import ru.patterns.core.service.currency.serialization.CurrencyWithRate
import ru.patterns.core.service.currency.serialization.ValCurs
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import java.util.function.Predicate

interface CurrencyService {
    fun convertCurrency(convertCurrency: ConvertCurrencyRequest): Mono<ConvertCurrencyResult>
    fun getCurrencyRate(code: String): Mono<GetCurrencyRateResult>

    sealed interface ConvertCurrencyResult {
        data class Success(val response: ConvertCurrencyResponse) : ConvertCurrencyResult
        sealed interface Error : ConvertCurrencyResult {
            data object BankUnavailable : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }

    sealed interface GetCurrencyRateResult {
        data class Success(val currencyWithRate: CurrencyWithRate) : GetCurrencyRateResult
        sealed interface Error : GetCurrencyRateResult {
            data object BankUnavailable : Error
            data object NonExistentCurrency : Error
            data class Unexpected(val cause: Throwable) : Error
        }
    }
}

@Service
class CurrencyServiceImpl(
    private val webClient: WebClient,
    private val currencyProperties: CurrencyProperties,
    circuitBreakerRegistry: CircuitBreakerRegistry
) : CurrencyService {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val mapper: XmlMapper = XmlMapper()
    private val circuitBreaker = circuitBreakerRegistry
        .createCircuitBreaker("currencyService") { error -> error is Exception }
    private val charCodeSet: Set<String> = getValuteCodes(currencyProperties.api.valute.url).block()!!

    companion object {
        const val RUB_CODE = "RUR"
        private val RUB_RATE = CurrencyWithRate(CurrencyCode(RUB_CODE), BigDecimal.ONE)
    }

    override fun convertCurrency(convertCurrency: ConvertCurrencyRequest): Mono<ConvertCurrencyResult> {
        log.info("Получен запрос на конвертацию валюты: {}", convertCurrency)

        return Mono.zip(
            getCurrencyRate(convertCurrency.currencyFrom.value),
            getCurrencyRate(convertCurrency.currencyTo.value)
        )
            .map<ConvertCurrencyResult> { tuple ->
                val fromRate = tuple.t1
                val toRate = tuple.t2
                if (fromRate !is GetCurrencyRateResult.Success)
                    throw IllegalArgumentException("Не удалось получить курс валюты")
                else if (toRate !is GetCurrencyRateResult.Success)
                    throw IllegalArgumentException("Не удалось получить курс валюты")

                val convertedAmount = convertCurrency.amount
                    .multiply(fromRate.currencyWithRate.rate)
                    .divide(toRate.currencyWithRate.rate, 2, RoundingMode.HALF_DOWN)

                log.info(
                    "Конвертация завершена: {} {} = {} {}",
                    convertCurrency.amount,
                    convertCurrency.currencyFrom,
                    convertedAmount,
                    convertCurrency.currencyTo
                )

                ConvertCurrencyResult.Success(
                    ConvertCurrencyResponse(
                        convertCurrency.currencyFrom,
                        convertCurrency.currencyTo,
                        convertedAmount
                    )
                )
            }
            .onErrorResume { error -> ConvertCurrencyResult.Error.Unexpected(error).toMono() }
    }

    override fun getCurrencyRate(code: String): Mono<GetCurrencyRateResult> {
        log.info("Получен запрос на получение валюты с кодом: {}", code)
        if (!charCodeSet.contains(code) || isInvalidCurrency(code)) {
            return GetCurrencyRateResult.Error.NonExistentCurrency.toMono()
        }

        return fetchCurrencyData(currencyProperties.api.currency.url)
            .map { xmlResponse -> getCurrencyRate(code, xmlResponse) }
            .map<GetCurrencyRateResult>(GetCurrencyRateResult::Success)
            .onErrorResume { error -> GetCurrencyRateResult.Error.Unexpected(error).toMono() }
    }

    private fun fetchCurrencyData(url: String): Mono<String> =
        webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String::class.java)
            .setCircuitBreaker(circuitBreaker)
            .doOnError { error -> log.error("Ошибка при получении курсов от ЦБ", error) }
            .cache(currencyProperties.cacheLifeTimeDuration)

    private fun getCurrencyRate(code: String, xmlResponse: String?): CurrencyWithRate {
        if (code.equals(RUB_CODE, ignoreCase = true)) {
            return RUB_RATE
        }

        val valCurs: ValCurs = getCurrency(xmlResponse)

        return valCurs.valutes.find { valute ->
            valute.charCode.equals(code, ignoreCase = true)
        }?.let { valute ->
            CurrencyWithRate(
                CurrencyCode(valute.charCode),
                BigDecimal(valute.value.replace(",", "."))
            )
        }
            ?: throw IllegalArgumentException("Отсутствует курс валюты по коду: $code")
    }

    private fun isInvalidCurrency(code: String): Boolean {
        try {
            Currency.getInstance(code)
            return false
        } catch (e: IllegalArgumentException) {
            return true
        }
    }

    private fun getValuteCodes(url: String): Mono<Set<String>> =
        fetchCurrencyData(url)
            .map { xmlResponse ->
                val list: CurrencyList = getCurrencyList(xmlResponse)

                list.items
                    .map { item -> item.isoCharCode }
                    .toSet()
                    .plus(RUB_CODE)
            }

    private fun getCurrency(xmlResponse: String?): ValCurs =
        try {
            mapper.readValue(xmlResponse, ValCurs::class.java)
        } catch (e: Exception) {
            log.error("Ошибка парсинга XML ответа", e)
            throw RuntimeException("При парсинге данных что-то пошло не так", e)
        }

    private fun getCurrencyList(xmlResponse: String?): CurrencyList =
        try {
            mapper.readValue(xmlResponse, CurrencyList::class.java)
        } catch (e: Exception) {
            log.error("Ошибка парсинга XML ответа", e)
            throw RuntimeException("При парсинге данных что-то пошло не так", e)
        }

    /**
     * Установить circuit breaker в реактивную цепочку.
     */
    private fun <T> Mono<T>.setCircuitBreaker(cb: CircuitBreaker): Mono<T> =
        this.transformDeferred(CircuitBreakerOperator.of(cb))

    private fun CircuitBreakerRegistry.createCircuitBreaker(
        configName: String,
        errorPredicate: Predicate<Throwable>
    ): CircuitBreaker {
        val baseConfig = this
            .getConfiguration(configName)
            .orElseThrow { NullPointerException("не найдена конфигурация $configName") }

        val configWithErrorPredicate = CircuitBreakerConfig
            .from(baseConfig)
            .recordException(errorPredicate)
            .build()

        return this
            .circuitBreaker(configName, configWithErrorPredicate)
    }
}