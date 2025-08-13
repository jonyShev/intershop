package com.jonyshev.intershop.config;

import com.jonyshev.intershop.paymentservice.api.PaymentApi;
import com.jonyshev.intershop.paymentservice.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    public ApiClient paymentApiClient(WebClient.Builder builder, @Value("${payment.base-url}") String baseUrl) {
        WebClient webClient = builder.build();
        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(baseUrl);
        return apiClient;
    }

    @Bean
    public PaymentApi paymentApi(ApiClient paymentApiClient) {
        return new PaymentApi(paymentApiClient);
    }
}