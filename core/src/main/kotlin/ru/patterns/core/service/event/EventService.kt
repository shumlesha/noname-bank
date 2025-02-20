package ru.patterns.core.service.event

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.patterns.core.events.AbstractEvent
import ru.patterns.core.service.event.repository.EventRepository

interface EventService {
    fun createEvent(event: AbstractEvent): Mono<Unit>
}

@Service
class EventServiceImpl(
    private val eventRepository: EventRepository
) : EventService {
    override fun createEvent(event: AbstractEvent) =
        eventRepository.save(event)
            .map { }
}