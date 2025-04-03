package com.unimagdalena.paymentservice.service;

import com.unimagdalena.paymentservice.entity.Payment;
import com.unimagdalena.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Flux<Payment> getAllPayments() {
        return Flux.defer(() -> Flux.fromIterable(paymentRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Payment> getPaymentById(String id) {
        return Mono.defer(() -> Mono.justOrEmpty(paymentRepository.findById(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Payment> createPayment(Payment payment) {
        payment.setId(UUID.randomUUID().toString());
        return Mono.defer(() -> Mono.just(paymentRepository.save(payment)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Payment> updatePayment(String id, Payment payment) {
        return Mono.defer(() -> {
            if (paymentRepository.existsById(id)) {
                payment.setId(id);
                return Mono.just(paymentRepository.save(payment));
            }
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deletePayment(String id) {
        return Mono.defer(() -> {
            paymentRepository.deleteById(id);
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
