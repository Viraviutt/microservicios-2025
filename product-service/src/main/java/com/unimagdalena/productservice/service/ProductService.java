package com.unimagdalena.productservice.service;

import com.unimagdalena.productservice.entity.Product;
import com.unimagdalena.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
                .subscribeOn(Schedulers.boundedElastic());
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