package com.jonyshev.paymentservice.controller;


import com.jonyshev.paymentservice.api.PaymentApi;
import com.jonyshev.paymentservice.model.BalanceResponse;
import com.jonyshev.paymentservice.model.PayRequest;
import com.jonyshev.paymentservice.model.PayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class PaymentController implements PaymentApi {

    private final AtomicReference<BigDecimal> balance;
    private final String currency;

    public PaymentController(
            @Value("${payment.initial-balance}") BigDecimal initial,
            @Value("${payment.currency}") String currency
    ) {
        this.balance = new AtomicReference<>(initial);
        this.currency = currency;
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(ServerWebExchange ex) {
        return Mono.just(ResponseEntity.ok(
                new BalanceResponse()
                        .amount(balance.get().doubleValue())
                        .currency(currency)
        ));
    }

    @Override
    public Mono<ResponseEntity<PayResponse>> pay(Mono<PayRequest> body, ServerWebExchange ex) {
        return body.map(req -> BigDecimal.valueOf(req.getAmount()))
                .map(need -> {
                    while (true) {
                        BigDecimal cur = balance.get();
                        if (cur.compareTo(need) < 0) {
                            return ResponseEntity.status(402).<PayResponse>build();
                        }
                        if (balance.compareAndSet(cur, cur.subtract(need))) {
                            return ResponseEntity.ok(
                                    new PayResponse()
                                            .success(true)
                                            .transactionId(UUID.randomUUID().toString())
                            );
                        }
                    }
                });
    }
}
