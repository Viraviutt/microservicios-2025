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
                .build();












        /*
        return builder.routes()
                // 1. Predicado básico por ruta
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("lb://order-service"))

                // 2. Predicado por metodo HTTP
                .route("product-admin", r -> r
                        .path("/api/products/**")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                        .uri("lb://product-admin-service"))

                // 3. Predicado por host
                .route("product-region", r -> r
                        .host("*.colombia.unimagdalena.com")
                        .and()
                        .path("/api/products/**")
                        .uri("lb://product-colombia-service"))

                // 4. Predicado por cabeceras HTTP
                .route("inventory-internal", r -> r
                        .path("/api/inventory/**")
                        .and()
                        .header("X-Role", "ADMIN")
                        .uri("lb://inventory-admin-service"))

                // 5. Predicado por cookies
                .route("payment-session", r -> r
                        .path("/api/payments/**")
                        .and()
                        .cookie("session-valid", "true")
                        .uri("lb://payment-service"))

                // 6. Predicado por parámetros de consulta
                .route("product-search", r -> r
                        .path("/api/products/search")
                        .and()
                        .query("region", "caribe")
                        .uri("lb://product-caribe-service"))

                // 7. Predicado por tiempo
                .route("promotions", r -> r
                        .path("/api/promotions/**")
                        .and()
                        .between(
                                ZonedDateTime.parse("2024-05-01T00:00:00-05:00"),
                                ZonedDateTime.parse("2024-12-31T23:59:59-05:00")
                        )
                        .uri("lb://promotion-service"))

                // 8. Predicado por peso (útil para pruebas A/B)
                .route("payment-new", r -> r
                        .path("/api/payments/**")
                        .and()
                        .weight("payment-group", 8)
                        .uri("lb://payment-new-service"))
                .route("payment-old", r -> r
                        .path("/api/payments/**")
                        .and()
                        .weight("payment-group", 2)
                        .uri("lb://payment-old-service"))

                // 9. Predicado con reescritura de ruta
                .route("api-legacy", r -> r
                        .path("/legacy-api/**")
                        .filters(f -> f.rewritePath("/legacy-api/(?<segment>.*)", "/api/v1/${segment}"))
                        .uri("lb://legacy-service"))

                // 10. Combinando múltiples predicados con operadores lógicos
                .route("vip-customers", r -> r
                        .path("/api/customers/**")
                        .and(p -> p
                                .header("X-Customer-Type", "VIP")
                                .or()
                                .query("premium", "true"))
                        .uri("lb://vip-customer-service"))

                .build();

         */
    }
}