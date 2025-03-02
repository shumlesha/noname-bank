package ru.patterns.core.atm

import reactor.core.publisher.Mono


interface EventProcessor<T> {
    fun process(event: T): Mono<ProcessResult>

    sealed interface ProcessResult {
        data object Success : ProcessResult
        data class Error(val cause: Throwable) : ProcessResult
    }
}