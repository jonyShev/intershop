package com.jonyshev.intershop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.WebFilter;

@Configuration
public class PrincipalToSessionConfig {

    public static final String PRINCIPAL_ATTR = "PRINCIPAL";

    @Bean
    public WebFilter principalInSessionFilter() {
        return (exchange, chain) ->
                ReactiveSecurityContextHolder.getContext()
                        .map(ctx -> ctx.getAuthentication().getName())
                        .defaultIfEmpty("anonymousUser")
                        .flatMap(username ->
                                exchange.getSession()
                                        .doOnNext(s -> s.getAttributes().put(PRINCIPAL_ATTR, username))
                                        .then(chain.filter(exchange))
                        );
    }
}
