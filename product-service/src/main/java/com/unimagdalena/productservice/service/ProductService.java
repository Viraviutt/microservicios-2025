package com.unimagdalena.productservice.service;

import com.unimagdalena.productservice.entity.Product;
import com.unimagdalena.productservice.repository.ProductRepository;
import com.unimagdalena.productservice.dto.InventoryCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient webClient;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.webClient = WebClient.builder().baseUrl("http://inventory-service:8082").build();
    }

    public Flux<Product> getAllProducts() {
        return Flux.defer(() -> Flux.fromIterable(productRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Product> getProductById(String id) {
        return Mono.defer(() -> Mono.justOrEmpty(productRepository.findById(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Product> createProduct(Product product) {
        product.setId(UUID.randomUUID().toString());
        return Mono.defer(() -> Mono.just(productRepository.save(product)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(savedProduct -> {
                    InventoryCreateRequest request = new InventoryCreateRequest(
                        savedProduct.getId(),
                        savedProduct.getName(),
                        0
                    );
                    return webClient.post()
                        .uri("/api/inventory")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .thenReturn(savedProduct);
                });
    }

    public Mono<Product> updateProduct(String id, Product product) {
        return Mono.defer(() -> {
            if (productRepository.existsById(id)) {
                product.setId(id);
                return Mono.just(productRepository.save(product));
            }
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteProduct(String id) {
        return Mono.defer(() -> {
            productRepository.deleteById(id);
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}