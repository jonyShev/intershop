package com.jonyshev.intershop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        // handler для редиректа после logout
        var logoutHandler = new RedirectServerLogoutSuccessHandler();
        logoutHandler.setLogoutSuccessUrl(URI.create("/")); // куда редиректить

        return http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeExchange(ex -> ex
                        .pathMatchers("/", "/main/**", "/item/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .pathMatchers("/cart/**", "/orders/**", "/buy/**").authenticated()
                        .anyExchange().permitAll()
                )
                .formLogin(withDefaults())               // дефолтная форма /login
                .logout(lo -> lo
                        .logoutUrl("/logout")                // по умолчанию POST /logout
                        .logoutSuccessHandler(logoutHandler) // вместо logoutSuccessUrl(...)
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}