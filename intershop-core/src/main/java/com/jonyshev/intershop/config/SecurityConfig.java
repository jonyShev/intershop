package com.jonyshev.intershop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

import java.net.URI;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        var logoutSuccess = new RedirectServerLogoutSuccessHandler();
        logoutSuccess.setLogoutSuccessUrl(URI.create("/"));

        return http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.GET, "/", "/main/**", "/main/items/**", "/items/**",
                                "/actuator/**", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/main/items/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/buy").authenticated()
                        .pathMatchers(HttpMethod.POST, "/items/**").authenticated()
                        .pathMatchers("/cart/**", "/orders/**").authenticated()
                        .anyExchange().denyAll()
                )
                .anonymous(withDefaults())   // чтобы был анонимный контекст на первом рендере
                .formLogin(withDefaults())
                .logout(lo -> lo.logoutUrl("/logout").logoutSuccessHandler(logoutSuccess))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}