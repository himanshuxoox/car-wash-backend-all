package com.carwash.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        // Log request
        logRequest(request);

        // Log response
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;
            logResponse(response, duration);
        }));
    }

    private void logRequest(ServerHttpRequest request) {
        log.info("╔════════════════════════════════════════════════════════════");
        log.info("║ 🚪 GATEWAY - INCOMING REQUEST");
        log.info("╠════════════════════════════════════════════════════════════");
        log.info("║ Method      : {}", request.getMethod());
        log.info("║ URI         : {}", request.getURI());
        log.info("║ Path        : {}", request.getPath());
        log.info("║ Remote Addr : {}", request.getRemoteAddress());
        log.info("╠════════════════════════════════════════════════════════════");
        log.info("║ HEADERS:");

        request.getHeaders().forEach((name, values) -> {
            values.forEach(value -> {
                if (name.equalsIgnoreCase("authorization")) {
                    value = maskToken(value);
                }
                log.info("║   {} = {}", name, value);
            });
        });

        log.info("╚════════════════════════════════════════════════════════════");
    }

    private void logResponse(ServerHttpResponse response, long duration) {
        log.info("╔════════════════════════════════════════════════════════════");
        log.info("║ 🚪 GATEWAY - OUTGOING RESPONSE");
        log.info("╠════════════════════════════════════════════════════════════");
        log.info("║ Status      : {}", response.getStatusCode());
        log.info("║ Duration    : {} ms", duration);
        log.info("╠════════════════════════════════════════════════════════════");
        log.info("║ HEADERS:");

        response.getHeaders().forEach((name, values) -> {
            values.forEach(value -> log.info("║   {} = {}", name, value));
        });

        log.info("╚════════════════════════════════════════════════════════════");
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 7) + "..." + token.substring(token.length() - 4);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}