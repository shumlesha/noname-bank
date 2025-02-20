package ru.patterns.core.events

import java.time.LocalDateTime
import java.util.UUID

abstract class AbstractEvent(
    val id: Long? = null,
    val aggregateId: UUID? = null,
    val eventType: EventType,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    open val payload: Any
)