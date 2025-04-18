package ru.patterns.corequery.utils.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import ru.patterns.corequery.utils.log.serialization.Factory.TraceDto
import ru.patterns.corequery.utils.log.serialization.TraceDto

class CustomLogger<T>(
    clazz: Class<T>,
    private val sender: KafkaLogSender
) {
    private val log: Logger = LoggerFactory.getLogger(clazz)

    fun infoWithTime(message: String, executionTimeMs: Long, methodName: String) {
        log.info(message)
        val requestId: String? = MDC.get("requestId")
        val traceDto = TraceDto(
            endpoint = methodName,
            responseTimeMs = executionTimeMs,
            requestId = requestId,
        )

        sendLogsAsync(traceDto)
    }

    fun methodExecutionTime(executionTimeMs: Long, methodName: String) {
        log.info("Метод $methodName выполнен за $executionTimeMs мс")
        val requestId: String? = MDC.get("requestId")
        val traceDto = TraceDto(
            endpoint = methodName,
            responseTimeMs = executionTimeMs,
            requestId = requestId,
        )

        sendLogsAsync(traceDto)
    }

    fun error(message: String, methodName: String) {
        log.error(message)
        val requestId: String? = MDC.get("requestId")
        val traceDto = TraceDto(
            endpoint = methodName,
            requestId = requestId,
        )

        sendLogsAsync(traceDto)
    }

    private fun sendLogsAsync(traceDto: TraceDto) {
        sender.sendObjectToKafkaAsync(traceDto)

        log.info("Логи отправлены в кафку")
    }
}