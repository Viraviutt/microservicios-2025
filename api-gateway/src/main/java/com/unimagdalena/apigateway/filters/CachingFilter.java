package com.unimagdalena.apigateway.filters;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de caching para almacenar respuestas de productos.
 * Este filtro intercepta las respuestas y las almacena en un caché para evitar
 * consultas repetidas a la base de datos.
 */

@Component
public class CachingFilter {

    // Cache para almacenar respuestas de productos
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    // Filtro de caching para la consulta de productos
    public GatewayFilter cachingFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String cacheKey = request.getURI().toString();

            if (cache.containsKey(cacheKey)) {
                String cachedResponse = cache.get(cacheKey);
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.OK);
                DataBuffer buffer = response.bufferFactory().wrap(cachedResponse.getBytes(StandardCharsets.UTF_8));
                return response.writeWith(Flux.just(buffer));
            }

            ServerHttpResponse originalResponse = exchange.getResponse();
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    return super.writeWith(Flux.from(body).doOnNext(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        cache.put(cacheKey, new String(bytes, StandardCharsets.UTF_8));
                    }));
                }
            };

            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        };
    }
}
