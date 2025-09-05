package com.jonyshev.intershop.service;

import com.jonyshev.intershop.paymentservice.api.PaymentApi;
import com.jonyshev.intershop.paymentservice.model.BalanceResponse;
import com.jonyshev.intershop.paymentservice.model.PayRequest;
import com.jonyshev.intershop.paymentservice.model.PayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceClient {

    private final PaymentApi paymentApi;

    @Value("${payment.currency:RUB}")
    private String currency;

    private Mono<String> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .defaultIfEmpty("anonymousUser");
    }

    public Mono<BalanceResponse> getBalance() {
        return currentUser()
                .flatMap(user -> paymentApi.getBalance(user));
    }

    public Mono<PayResponse> pay(BigDecimal amount) {
        return currentUser()
                .flatMap(user -> {
                    PayRequest req = new PayRequest()
                            .amount(amount.doubleValue())
                            .currency(currency);
                    return paymentApi.pay(user, req);
                });
    }
}
