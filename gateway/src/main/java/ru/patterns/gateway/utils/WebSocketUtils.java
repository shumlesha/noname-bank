package ru.patterns.gateway.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

@UtilityClass
public class WebSocketUtils {
    public boolean isWebSocketUpgrade(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        return headers.containsKey("Upgrade") &&
                "websocket".equalsIgnoreCase(headers.getFirst("Upgrade"));
    }
}
