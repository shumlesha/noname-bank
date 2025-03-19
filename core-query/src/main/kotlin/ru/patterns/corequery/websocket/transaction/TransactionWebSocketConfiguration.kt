package ru.patterns.corequery.websocket.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.patterns.corequery.config.TransactionWebSocketProperties
import ru.patterns.corequery.domain.AccountId
import ru.patterns.corequery.service.transaction.repository.TransactionRepository
import java.util.UUID


@Configuration
@EnableWebFlux
class ReactiveWebSocketConfig(
    private val properties: TransactionWebSocketProperties
) {

    @Bean
    fun handlerMapping(webSocketHandler: ReactiveWebSocketHandler): SimpleUrlHandlerMapping =
        SimpleUrlHandlerMapping()
            .apply {
                urlMap = mapOf(
                    properties.client.path to webSocketHandler,
                    properties.employee.path to webSocketHandler
                )
                order = -1
            }

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter = WebSocketHandlerAdapter()
}

@Component
class ReactiveWebSocketHandler(
    private val transactionRepo: TransactionRepository,
    private val objectMapper: ObjectMapper
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> =
        session.receive()
            .flatMap { message ->
                val accountId = message.payloadAsText
                val response = transactionRepo.findAllByAccountId(AccountId(UUID.fromString(accountId)))
                    .flatMapMany { result ->
                        when (result) {
                            is TransactionRepository.FindAllTransactionResult.Success ->
                                Flux.fromIterable(result.transactions)

                            else -> Flux.empty()
                        }
                    }
                    .map { transaction -> session.textMessage(objectMapper.writeValueAsString(transaction)) }

                session.send(response)
            }
            .then()
}