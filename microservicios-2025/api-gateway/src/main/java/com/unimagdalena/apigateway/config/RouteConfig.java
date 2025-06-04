package com.unimagdalena.apigateway.config;

import com.unimagdalena.apigateway.filter.factory.ProductCachingFilterFactory;
import com.unimagdalena.apigateway.filter.factory.ServiceAuthHeaderFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
                                           ServiceAuthHeaderFilterFactory authFilterFactory, ProductCachingFilterFactory productCachingFilterFactory) {
        return builder.routes()
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .filter(authFilterFactory.apply(c -> {
                                    c.setHeaderName("X-Service-Auth");
                                    c.setHeaderValue("order-service-key");
                                })))
                        .uri("lb://order-service"))
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.filter(productCachingFilterFactory.apply(c -> {
                            c.setTtlSeconds(300);
                        })))
                        .uri("lb://product-service"))
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .uri("lb://inventory-service"))
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .uri("lb://payment-service"))
              .route("keycloak-token", r -> r
                .path("/auth")
                .filters(f -> f.setPath("/realms/master/protocol/openid-connect/token"))
                .uri("http://keycloak:8080"))
                .build();

    }
}