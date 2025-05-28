package com.unimagdalena.paymentservice.service;


import com.unimagdalena.paymentservice.entity.Payment;
import com.unimagdalena.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment1;
    private Payment payment2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        payment1 = new Payment();
        payment1.setId("1");
        payment1.setOrderId(1001L);
        payment1.setAmount(BigDecimal.valueOf(100.00));

        payment2 = new Payment();
        payment2.setId("2");
        payment2.setOrderId(2002L);
        payment2.setAmount(BigDecimal.valueOf(200.00));
    }

    @Test
    void getAllPayments_shouldReturnAllPayments() {
        // Setup - mock repository to return list of payments
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));

        // Execute and verify
        StepVerifier.create(paymentService.getAllPayments())
                .expectNext(payment1)
                .expectNext(payment2)
                .verifyComplete();

        // Verify repository interaction
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void getPaymentById_whenPaymentExists_shouldReturnPayment() {
        // Setup - mock repository to return payment for ID "1"
        when(paymentRepository.findById("1")).thenReturn(Optional.of(payment1));

        // Execute and verify
        StepVerifier.create(paymentService.getPaymentById("1"))
                .expectNext(payment1)
                .verifyComplete();

        // Verify repository interaction
        verify(paymentRepository, times(1)).findById("1");
    }

    @Test
    void getPaymentById_whenPaymentDoesNotExist_shouldReturnEmpty() {
        // Setup - mock repository to return empty optional for non-existent ID
        when(paymentRepository.findById("999")).thenReturn(Optional.empty());

        // Execute and verify
        StepVerifier.create(paymentService.getPaymentById("999"))
                .verifyComplete();

        // Verify repository interaction
        verify(paymentRepository, times(1)).findById("999");
    }

    @Test
    void createPayment_shouldAssignIdAndSavePayment() {
        // Setup - create payment without ID and setup mock
        Payment newPayment = new Payment();
        newPayment.setOrderId(3003L);
        newPayment.setAmount(BigDecimal.valueOf(300.00));

        // Mock the save method to return the payment with ID
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            return savedPayment; // Return the payment that was passed to save()
        });

        // Execute and verify
        StepVerifier.create(paymentService.createPayment(newPayment))
                .expectNextMatches(payment ->
                        payment.getId() != null &&
                                payment.getOrderId().equals(3003L) &&
                                payment.getAmount().equals(BigDecimal.valueOf(300.00))
                )
                .verifyComplete();

        // Verify repository interaction
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void updatePayment_whenPaymentExists_shouldUpdateAndReturnPayment() {
        // Setup - create updated payment and mock repository responses
        Payment updatedPayment = new Payment();
        updatedPayment.setId("1");
        updatedPayment.setOrderId(1001L);
        updatedPayment.setAmount(BigDecimal.valueOf(150.00));

        when(paymentRepository.existsById("1")).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        // Execute and verify
        StepVerifier.create(paymentService.updatePayment("1", updatedPayment))
                .expectNext(updatedPayment)
                .verifyComplete();

        // Verify repository interaction
        verify(paymentRepository, times(1)).existsById("1");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void updatePayment_whenPaymentDoesNotExist_shouldReturnEmpty() {
        // Setup - mock repository to indicate payment does not exist
        Payment updatedPayment = new Payment();
        updatedPayment.setId("999");
        updatedPayment.setOrderId(9999L);
        updatedPayment.setAmount(BigDecimal.valueOf(999.00));

        when(paymentRepository.existsById("999")).thenReturn(false);

        // Execute and verify
        StepVerifier.create(paymentService.updatePayment("999", updatedPayment))
                .verifyComplete();

        // Verify repository interaction
        verify(paymentRepository, times(1)).existsById("999");
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void deletePayment_shouldDeletePayment() {
        // Setup - mock repository deleteById method
        doNothing().when(paymentRepository).deleteById("1");

        // Execute and verify
        StepVerifier.create(paymentService.deletePayment("1"))
                .verifyComplete();

        // Verify repository interaction
        verify(paymentRepository, times(1)).deleteById("1");
    }
}
