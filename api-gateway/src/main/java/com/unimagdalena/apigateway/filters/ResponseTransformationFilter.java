package com.unimagdalena.apigateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filtro para transformar la respuesta.
 * Este filtro agrega un encabezado "correlationId" a la respuesta.
 */

@Component
public class ResponseTransformationFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).doOnSuccess(aVoid -> {
            ServerHttpResponse response = exchange.getResponse();
            String correlationId = exchange.getRequest().getHeaders().getFirst("correlationId");
            if (correlationId != null) {
                response.getHeaders().add("correlationId", correlationId);
            }
        });
    }
/*
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).doOnSuccess(aVoid -> {
            ServerHttpResponse response = exchange.getResponse();
            String correlationId = exchange.getRequest().getHeaders().getFirst("correlationId");
            if (correlationId != null) {
                response.getHeaders().add("correlationId", correlationId);
            }
        });
    }
    */

}
