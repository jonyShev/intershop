package com.jonyshev.intershop.service;

import com.jonyshev.intershop.paymentservice.api.PaymentApi;
import com.jonyshev.intershop.paymentservice.model.PayRequest;
import com.jonyshev.intershop.paymentservice.model.PayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class PaymentServiceClient {

    private final PaymentApi paymentApi;
    private final String currency;

    public PaymentServiceClient(PaymentApi paymentApi, @Value("${payment.currency:EUR}") String currency) {
        this.paymentApi = paymentApi;
        this.currency = currency;
    }

    public Mono<BigDecimal> getBalance() {
        return paymentApi.getBalance()
                .map(r -> BigDecimal.valueOf(r.getAmount()));
    }

    public Mono<Boolean> pay(BigDecimal amount) {
        PayRequest body = new PayRequest()
                .amount(amount.doubleValue())
                .currency(currency);

        return paymentApi.pay(body)
                .map(PayResponse::getSuccess)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.PAYMENT_REQUIRED) {
                        return Mono.just(false);
                    }
                    return Mono.error(ex);
                });
    }


}
