package com.jonyshev.intershop.security;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserAuthorityRepository extends ReactiveCrudRepository<UserAuthority, Long> {
    Flux<UserAuthority> findByUserId(UUID userId);
}
