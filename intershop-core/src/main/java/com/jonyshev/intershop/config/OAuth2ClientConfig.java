package com.jonyshev.intershop.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;


@Configuration
@ConditionalOnProperty(name = "oauth2.enabled", havingValue = "true", matchIfMissing = true)
public class OAuth2ClientConfig {

    @Bean
    ReactiveOAuth2AuthorizedClientManager authorizedClientManager(ReactiveClientRegistrationRepository registrations,
                                                                  ReactiveOAuth2AuthorizedClientService clients) {
        return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(registrations, clients);
    }

    @Bean
    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2(ReactiveOAuth2AuthorizedClientManager manager) {
        var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(manager);
        oauth.setDefaultClientRegistrationId("payments");
        return oauth;
    }
}
