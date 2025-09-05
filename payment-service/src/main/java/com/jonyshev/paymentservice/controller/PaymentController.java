package com.jonyshev.paymentservice.controller;


import com.jonyshev.paymentservice.api.PaymentApi;
import com.jonyshev.paymentservice.model.BalanceResponse;
import com.jonyshev.paymentservice.model.PayRequest;
import com.jonyshev.paymentservice.model.PayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class PaymentController implements PaymentApi {

    private final String currency;
    private final ConcurrentHashMap<String, AtomicReference<BigDecimal>> balances = new ConcurrentHashMap<>();

    public PaymentController(@Value("${payment.initial-balance:3000.00}") BigDecimal initial,
                             @Value("${payment.currency:RUB}") String currency) {
        this.currency = currency;
        this.initial = initial;
    }

    private final BigDecimal initial;

    private AtomicReference<BigDecimal> balanceRef(String user) {
        return balances.computeIfAbsent(user, u -> new AtomicReference<>(initial));
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(@RequestHeader("X-User") String xUser, ServerWebExchange exchange) {
        BigDecimal amount = balanceRef(xUser).get();
        return Mono.just(ResponseEntity.ok(
                new BalanceResponse().amount(amount.doubleValue()).currency(currency)
        ));
    }

    @Override
    public Mono<ResponseEntity<PayResponse>> pay(@RequestHeader("X-User") String xUser, Mono<PayRequest> body, ServerWebExchange exchange) {
        return body.map(req -> BigDecimal.valueOf(req.getAmount()))
                .map(need -> {
                    var ref = balanceRef(xUser);
                    while (true) {
                        BigDecimal cur = ref.get();
                        if (cur.compareTo(need) < 0) {
                            return ResponseEntity.status(402).<PayResponse>build();
                        }
                        if (ref.compareAndSet(cur, cur.subtract(need))) {
                            return ResponseEntity.ok(new PayResponse()
                                    .success(true)
                                    .transactionId(UUID.randomUUID().toString()));
                        }
                    }
                });
    }
}

