package com.unimagdalena.apigateway.filter.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = UUID.randomUUID().toString();
        logger.info("📌 Nueva solicitud con CorrelationId: {}", correlationId);

        // Agregar el ID a la solicitud
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .build();

        // Crear un intercambio decorado para la respuesta
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request)
                .response(new ServerHttpResponseDecorator(exchange.getResponse()) {
                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders httpHeaders = super.getHeaders();
                        if (!httpHeaders.containsKey(CORRELATION_ID_HEADER)) {
                            httpHeaders.add(CORRELATION_ID_HEADER, correlationId);
                        }
                        if (!httpHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
                            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
                        }
                        return httpHeaders;
                    }
                })
                .build();

        // Continuar con la cadena de filtros
        return chain.filter(mutatedExchange)
                .doOnSuccess(v -> logger.info("✅ Respuesta con CorrelationId: {} - Headers: {}",
                        correlationId, mutatedExchange.getResponse().getHeaders()));
    }

    @Override
    public int getOrder() {
        return -100; // Alta prioridad
    }
}
