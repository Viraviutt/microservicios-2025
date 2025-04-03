package com.unimagdalena.productservice.service;

import com.unimagdalena.productservice.entity.Product;
import com.unimagdalena.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        // given
        String uuid = UUID.randomUUID().toString();

        Product productToSave = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .build();

        Product savedProduct = Product.builder()
                .id(uuid)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when & then
        StepVerifier.create(productService.createProduct(productToSave))
                .expectNextMatches(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getId()).isEqualTo(uuid);
                    assertThat(result.getName()).isEqualTo("Test Product");
                    assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("100.00"));
                    return true;
                })
                .verifyComplete();

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldGetAllProducts() {
        // given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();

        Product product1 = Product.builder()
                .id(uuid1)
                .name("Product 1")
                .description("Description 1")
                .price(new BigDecimal("100.00"))
                .build();

        Product product2 = Product.builder()
                .id(uuid2)
                .name("Product 2")
                .description("Description 2")
                .price(new BigDecimal("200.00"))
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        // when & then
        StepVerifier.create(productService.getAllProducts())
                .expectNext(product1, product2)
                .verifyComplete();

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldGetProductById() {
        // given
        String uuid = UUID.randomUUID().toString();

        Product product = Product.builder()
                .id(uuid)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .build();

        when(productRepository.findById(uuid)).thenReturn(Optional.of(product));

        // when & then
        StepVerifier.create(productService.getProductById(uuid))
                .expectNextMatches(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getId()).isEqualTo(uuid);
                    assertThat(result.getName()).isEqualTo("Test Product");
                    assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("100.00"));
                    return true;
                })
                .verifyComplete();

        verify(productRepository, times(1)).findById(uuid);
    }

    @Test
    void shouldGetEmptyMonoWhenProductNotFound() {
        // given
        String uuid = UUID.randomUUID().toString();
        when(productRepository.findById(uuid)).thenReturn(Optional.empty());

        // when & then
        StepVerifier.create(productService.getProductById(uuid))
                .verifyComplete();

        verify(productRepository, times(1)).findById(uuid);
    }

    @Test
    void shouldUpdateProduct() {
        // given
        String uuid = UUID.randomUUID().toString();

        Product productToUpdate = Product.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("150.00"))
                .build();

        Product updatedProduct = Product.builder()
                .id(uuid)
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("150.00"))
                .build();

        when(productRepository.existsById(uuid)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // when & then
        StepVerifier.create(productService.updateProduct(uuid, productToUpdate))
                .expectNextMatches(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getId()).isEqualTo(uuid);
                    assertThat(result.getName()).isEqualTo("Updated Product");
                    assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
                    return true;
                })
                .verifyComplete();

        verify(productRepository, times(1)).existsById(uuid);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldReturnEmptyMonoWhenUpdatingNonExistentProduct() {
        // given
        String uuid = UUID.randomUUID().toString();

        Product productToUpdate = Product.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("150.00"))
                .build();

        when(productRepository.existsById(uuid)).thenReturn(false);

        // when & then
        StepVerifier.create(productService.updateProduct(uuid, productToUpdate))
                .verifyComplete();

        verify(productRepository, times(1)).existsById(uuid);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() {
        // given
        String uuid = UUID.randomUUID().toString();
        doNothing().when(productRepository).deleteById(uuid);

        // when & then
        StepVerifier.create(productService.deleteProduct(uuid))
                .verifyComplete();

        verify(productRepository, times(1)).deleteById(uuid);
    }
}
