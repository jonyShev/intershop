package com.jonyshev.intershop.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class DbReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository users;
    private final UserAuthorityRepository authorities;

    public DbReactiveUserDetailsService(UserRepository users, UserAuthorityRepository authorities) {
        this.users = users;
        this.authorities = authorities;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return users.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)))
                .flatMap(u -> authorities.findByUserId(u.getId()).collectList()
                        .map(auths -> User
                                .withUsername(u.getUsername())
                                .password(u.getPassword())
                                .authorities(auths.stream()
                                        .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                                        .collect(Collectors.toList()))
                                .disabled(!u.isEnabled())
                                .build()
                        )
                );
    }
}
