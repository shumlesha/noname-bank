package ru.patterns.core.service.event.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import ru.patterns.core.events.AbstractEvent

@Repository
interface EventRepository : R2dbcRepository<AbstractEvent, Long>