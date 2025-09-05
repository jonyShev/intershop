package com.jonyshev.intershop.config;

import com.jonyshev.intershop.paymentservice.api.PaymentApi;
import com.jonyshev.intershop.paymentservice.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(name = "intershop.payment.client.enabled", havingValue = "true", matchIfMissing = true)
public class PaymentClientConfig {

    @Bean
    public ApiClient paymentApiClient(WebClient.Builder builder,
                                      ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2,
                                      @Value("${payment.base-url}") String baseUrl) {
        ExchangeFilterFunction userHeader = (request, next) ->
                ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                        .map(auth -> auth != null ? auth.getName() : "anonymousUser")
                        .defaultIfEmpty("anonymousUser")
                        .flatMap(u -> {
                            ClientRequest withUser = ClientRequest.from(request)
                                    .headers(h -> h.set("X-User", u))
                                    .build();
                            return next.exchange(withUser);
                        });

        WebClient webClient = builder
                .filter(userHeader)
                .filter(oauth2)
                .build();

        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(baseUrl);
        return apiClient;
    }

    @Bean
    public PaymentApi paymentApi(ApiClient paymentApiClient) {
        return new PaymentApi(paymentApiClient);
    }
}