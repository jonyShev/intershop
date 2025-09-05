package com.jonyshev.intershop.security;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<AppUser, UUID> {
    Mono<AppUser> findByUsername(String username);
}
