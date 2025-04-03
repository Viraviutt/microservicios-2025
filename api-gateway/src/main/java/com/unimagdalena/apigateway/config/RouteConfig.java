package com.unimagdalena.apigateway.config;

import com.unimagdalena.apigateway.filters.CachingFilter;
import com.unimagdalena.apigateway.filters.RequestTransformationFilter;
import com.unimagdalena.apigateway.filters.ResponseTransformationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RouteConfig {

    private final CachingFilter cachingFilter;

    private final RequestTransformationFilter requestTransformationFilter;

    private final ResponseTransformationFilter responseTransformationFilter;


    public RouteConfig(CachingFilter cachingFilter, RequestTransformationFilter requestTransformationFilter, ResponseTransformationFilter responseTransformationFilter) {
        this.cachingFilter = cachingFilter;
        this.requestTransformationFilter = requestTransformationFilter;
        this.responseTransformationFilter = responseTransformationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f.filter(requestTransformationFilter)
                                                        .filter(responseTransformationFilter))
                        .uri("lb://order-service"))
                .route("product-service-cache", r -> r
                        .path("/api/products")
                        .and()
                        .method("GET")
                        .filters(f -> f.filter(requestTransformationFilter)
                                .filter(responseTransformationFilter)
                                .filter(cachingFilter.cachingFilter()))
                        .uri("lb://product-service"))

                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.filter(requestTransformationFilter)
                                                        .filter(responseTransformationFilter))
                        .uri("lb://product-service"))
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .filters(f -> f.filter(requestTransformationFilter)
                                                        .filter(responseTransformationFilter))
                        .uri("lb://inventory-service"))
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f.filter(requestTransformationFilter)
                                                        .filter(responseTransformationFilter))
                        .uri("lb://payment-service"))
                .build();
    }
}