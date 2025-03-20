package ru.patterns.gateway.exception;

import jakarta.ws.rs.NotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
@Slf4j
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status;
        String errorMessage;

        if (ex instanceof ResponseStatusException statusException) {
            status = (HttpStatus) statusException.getStatusCode();
            errorMessage = ex.getMessage();
        } else if (ex instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorMessage = "Requested resource not found";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMessage = "Internal server error";
        }

        response.setStatusCode(status);

        log.error("Gateway error: {}", ex.getMessage(), ex);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(FORMATTER));
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", errorMessage);
        errorResponse.put("path", exchange.getRequest().getPath().value());

        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                StringBuilder json = new StringBuilder("{");
                errorResponse.forEach((key, value) ->
                        json.append("\"")
                                .append(key)
                                .append("\":\"")
                                .append(value)
                                .append("\",")
                );

                String finalJson = json.substring(0, json.length() - 1) + "}";

                return bufferFactory.wrap(finalJson.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("Error writing response", e);
                return bufferFactory.wrap("".getBytes());
            }
        }));
    }
}
