package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Asegura que los mocks están inicializados

        product = new Product();
        product.setId("1");  // Usa una ID válida
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(50);
    }

    @Test
    void shouldSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Mono<Product> savedProduct = productService.createProduct(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.block().getName()).isEqualTo("Test Product");
    }

    @Test
    void shouldFindProductById() {
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        Mono<Optional<Product>> foundProduct = productService.getProductById("1");
        assertThat(foundProduct.block()).isNotNull();
        //assertThat(foundProduct).isIn();
        assertThat(foundProduct.block().get().getName()).isEqualTo("Test Product");
    }
}
