package com.unimagdalena.apigateway.filter.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ProductCachingFilterFactory extends AbstractGatewayFilterFactory<ProductCachingFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(ProductCachingFilterFactory.class);
    private final DefaultDataBufferFactory bufferFactory = new DefaultDataBufferFactory();

    @Autowired
    private ReactiveRedisTemplate<String, byte[]> redisTemplate;

    public ProductCachingFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            String clientIp = request.getRemoteAddress() != null ?
                    request.getRemoteAddress().getAddress().getHostAddress() : "unknown";

            if (request.getMethod() == HttpMethod.GET && path.matches("/api/products/\\d+")) {
                ReactiveValueOperations<String, byte[]> ops = redisTemplate.opsForValue();
                return ops.get(path)
                        .flatMap(cachedBody -> {
                            if (cachedBody != null) {
                                log.info("CACHE HIT: {} | Cliente: {}", path, clientIp);
                                ServerHttpResponse response = exchange.getResponse();
                                response.getHeaders().add("X-Cache", "HIT");
                                DataBuffer buffer = bufferFactory.wrap(cachedBody);
                                return response.writeWith(Mono.just(buffer));
                            }
                            log.info("CACHE MISS: {} | Cliente: {} | Cargando desde servicio", path, clientIp);

                            ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
                                @Override
                                public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                                    if (getStatusCode() != null && getStatusCode().is2xxSuccessful()) {
                                        return Flux.from(body)
                                                .collectList()
                                                .flatMap(dataBuffers -> {
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

                                                    ops.set(path, allBytes, Duration.ofSeconds(config.getTtlSeconds())).subscribe();
                                                    log.info("Respuesta para {} almacenada en caché, TTL: {} segundos", path, config.getTtlSeconds());
                                                    DataBuffer buffer = bufferFactory.wrap(allBytes);
                                                    return super.writeWith(Mono.just(buffer));
                                                });
                                    }
                                    return super.writeWith(body);
                                }
                            };

                            ServerWebExchange modifiedExchange = exchange.mutate()
                                    .response(responseDecorator)
                                    .build();

                            return chain.filter(modifiedExchange);
                        });
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        private long ttlSeconds = 60;

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }
}