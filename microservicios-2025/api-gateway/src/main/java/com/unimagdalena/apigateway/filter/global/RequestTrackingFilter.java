package com.unimagdalena.apigateway.filter.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestTrackingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestTrackingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        log.info("Solicitud entrante [{}]: {} {}",
                requestId,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());

        return chain.filter(
                exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-Request-ID", requestId)
                                .build())
                        .build()
        ).doFinally(signalType -> {
            log.info("Solicitud completada [{}] con estado: {}",
                    requestId,
                    exchange.getResponse().getStatusCode());
        });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}