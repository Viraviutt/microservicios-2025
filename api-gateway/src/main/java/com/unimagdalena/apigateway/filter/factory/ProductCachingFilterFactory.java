package com.unimagdalena.apigateway.filter.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class ProductCachingFilterFactory extends AbstractGatewayFilterFactory<ProductCachingFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(ProductCachingFilterFactory.class);
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();

    public ProductCachingFilterFactory() {
        super(Config.class);
    }

    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", cache.size());
        stats.put("cachedEntries", cache.keySet());

        // Contar entradas válidas e inválidas
        long validEntries = cache.entrySet().stream()
                .filter(entry -> !entry.getValue().isExpired())
                .count();

        stats.put("validEntries", validEntries);
        stats.put("expiredEntries", cache.size() - validEntries);

        return stats;
    }

    public void clearCache() {
        cache.clear();
        log.info("Cache limpiado manualmente");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            String clientIp = request.getRemoteAddress() != null ?
                    request.getRemoteAddress().getAddress().getHostAddress() : "unknown";

            if (request.getMethod() == HttpMethod.GET && path.matches("/api/products/\\d+")) {
                CacheEntry entry = cache.get(path);
                if (entry != null && !entry.isExpired()) {
                    long ttlRemaining = (entry.expirationTime - System.currentTimeMillis()) / 1000;
                    log.info("🎯 CACHE HIT: {} | Cliente: {} | TTL restante: {}s",
                            path, clientIp, ttlRemaining);
                    return entry.applyCachedResponse(exchange);
                } else {
                    log.info("❌ CACHE MISS: {} | Cliente: {} | Cargando desde servicio", path, clientIp);
                }

                // No existe caché, capturar la respuesta
                ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
                    @Override
                    public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                        if (getStatusCode() != null && getStatusCode().is2xxSuccessful()) {
                            return Flux.from(body)
                                    .collectList()
                                    .flatMap(dataBuffers -> {
                                        // Unir todos los buffers
                                        byte[] allBytes = dataBuffers.stream()
                                                .map(buffer -> {
                                                    byte[] bytes = new byte[buffer.readableByteCount()];
                                                    buffer.read(bytes);
                                                    return bytes;
                                                })
                                                .reduce(new byte[0], (acc, bytes) -> {
                                                    byte[] newAcc = new byte[acc.length + bytes.length];
                                                    System.arraycopy(acc, 0, newAcc, 0, acc.length);
                                                    System.arraycopy(bytes, 0, newAcc, acc.length, bytes.length);
                                                    return newAcc;
                                                });

                                        // Guardar en caché
                                        HttpHeaders headers = new HttpHeaders();
                                        headers.add("X-Cache", "MISS");
                                        headers.addAll(getHeaders());
                                        cache.put(path, new CacheEntry(allBytes, headers,
                                                TimeUnit.SECONDS.toMillis(config.getTtlSeconds())));

                                        log.info("Respuesta para {} almacenada en caché, TTL: {} segundos",
                                                path, config.getTtlSeconds());

                                        // Devolver la respuesta original
                                        DataBuffer buffer = bufferFactory.wrap(allBytes);
                                        return super.writeWith(Mono.just(buffer));
                                    });
                        }
                        return super.writeWith(body);
                    }
                };

                // Reemplazar la respuesta con el decorador
                ServerWebExchange modifiedExchange = exchange.mutate()
                        .response(responseDecorator)
                        .build();

                return chain.filter(modifiedExchange);
            }

            // Para solicitudes no cacheables, continuar normalmente
            return chain.filter(exchange);
        };
    }

    public static class Config {
        private long ttlSeconds = 60; // Tiempo de vida por defecto: 60 segundos

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }

    private class CacheEntry {
        private final byte[] body;
        private final HttpHeaders headers;
        private final long expirationTime;

        public CacheEntry(byte[] body, HttpHeaders headers, long ttlMillis) {
            this.body = body;
            this.headers = new HttpHeaders();
            this.headers.addAll(headers);
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        public Mono<Void> applyCachedResponse(ServerWebExchange exchange) {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().putAll(this.headers);

            // Agregar encabezados indicativos del caché
            response.getHeaders().add("X-Cache", "HIT");
            response.getHeaders().add("X-Cache-Expires", String.valueOf(expirationTime));

            DataBuffer buffer = bufferFactory.wrap(this.body);
            return response.writeWith(Mono.just(buffer));
        }
    }
}