package com.jonyshev.intershop.service;

import com.jonyshev.intershop.AbstractRedisIntegrationTest;
import com.jonyshev.intershop.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class ItemServiceIntegrationTest extends AbstractRedisIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ReactiveRedisTemplate<String, ItemDto> redisTemplate;

    @MockitoBean
    private CartService cartService;

    private final String key = "item:1";
    private final WebSession session = null;

    @BeforeEach
    void setup() {
        redisTemplate.delete(key).block();
        when(cartService.getCountForItem(anyLong(), any())).thenReturn(Mono.just(0));
    }

    @Test
    void shouldCacheItemAfterFirstAccess() {
        StepVerifier.create(redisTemplate.opsForValue().get(key))
                .expectNextCount(0) // ничего не должно быть
                .verifyComplete();

        Mono<ItemDto> result = itemService.getItemDtoById(1L, session);

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getId().equals(1L))
                .verifyComplete();

        StepVerifier.create(redisTemplate.opsForValue().get(key))
                .expectNextMatches(dto -> dto.getId().equals(1L))
                .verifyComplete();
    }
}