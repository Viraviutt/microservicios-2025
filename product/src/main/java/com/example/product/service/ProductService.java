package com.example.product.service;



import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<Product> getAllProducts() {
        //return Flux.fromIterable(productRepository.findAll());
        return Flux.fromIterable(productRepository.findAll());
    }

    public Mono<Optional<Product>> getProductById(String id) {
        return Mono.just(productRepository.findById(id));
    }

    public Mono<Product> createProduct(Product product) {
        return Mono.just(productRepository.save(product));
    }
}