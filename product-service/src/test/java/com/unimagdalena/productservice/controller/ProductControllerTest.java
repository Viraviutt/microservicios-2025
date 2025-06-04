package com.unimagdalena.productservice.controller;

import com.unimagdalena.productservice.entity.Product;
import com.unimagdalena.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        // given
        String uuid = UUID.randomUUID().toString();
        Product request = new Product();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(new BigDecimal("100.0"));

        Product response = new Product();
        response.setId(uuid);
        response.setName("Test Product");
        response.setDescription("Test Description");
        response.setPrice(new BigDecimal("100.0"));

        when(productService.createProduct(any(Product.class))).thenReturn(Mono.just(response));

        // when & then
        webTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(uuid)
                .jsonPath("$.name").isEqualTo("Test Product")
                .jsonPath("$.price").isEqualTo(100.0);
    }

    @Test
    void shouldGetAllProducts() {
        // given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();

        Product product1 = new Product();
        product1.setId(uuid1);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("100.0"));

        Product product2 = new Product();
        product2.setId(uuid2);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("200.0"));

        when(productService.getAllProducts()).thenReturn(Flux.fromIterable(Arrays.asList(product1, product2)));

        // when & then
        webTestClient.get().uri("/api/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(2)
                .contains(product1, product2);
    }

    @Test
    void shouldGetProductById() {
        // given
        String uuid = UUID.randomUUID().toString();
        Product product = new Product();
        product.setId(uuid);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("100.0"));

        when(productService.getProductById(anyString())).thenReturn(Mono.just(product));

        // when & then
        webTestClient.get().uri("/api/products/{id}", uuid)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(uuid)
                .jsonPath("$.name").isEqualTo("Test Product")
                .jsonPath("$.price").isEqualTo(100.0);
    }

    @Test
    void shouldUpdateProduct() {
        // given
        String uuid = UUID.randomUUID().toString();
        Product request = new Product();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setPrice(new BigDecimal("150.0"));

        Product response = new Product();
        response.setId(uuid);
        response.setName("Updated Product");
        response.setDescription("Updated Description");
        response.setPrice(new BigDecimal("150.0"));

        when(productService.updateProduct(anyString(), any(Product.class))).thenReturn(Mono.just(response));

        // when & then
        webTestClient.put().uri("/api/products/{id}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(uuid)
                .jsonPath("$.name").isEqualTo("Updated Product")
                .jsonPath("$.price").isEqualTo(150.0);
    }

    @Test
    void shouldDeleteProduct() {
        // given
        String uuid = UUID.randomUUID().toString();
        when(productService.deleteProduct(anyString())).thenReturn(Mono.empty());

        // when & then
        webTestClient.delete().uri("/api/products/{id}", uuid)
                .exchange()
                .expectStatus().isNoContent();
    }
}
