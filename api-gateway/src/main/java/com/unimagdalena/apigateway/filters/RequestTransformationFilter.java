package com.unimagdalena.apigateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Filtro para transformar la request.
 * Este filtro agrega un encabezado "correlationId" a la request.
 */

@Component
public class RequestTransformationFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Agregar correlationId a la request
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("correlationId", UUID.randomUUID().toString())
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
/*
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Agregar correlationId a la request
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("correlationId", UUID.randomUUID().toString())
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
    */

}