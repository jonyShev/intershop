package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ReactiveRedisTemplate<String, ItemDto> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, ItemDto> valueOps;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private WebSession session;

    private Item item1;

    @BeforeEach
    void setup() {
        // Делаем, чтобы opsForValue всегда возвращал мок valueOps
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        // Дефолтное поведение для get/set
        when(valueOps.get(anyString())).thenReturn(Mono.empty());
        when(valueOps.set(anyString(), any(ItemDto.class), any(Duration.class))).thenReturn(Mono.just(true));

        // Тестовый товар
        item1 = Item.builder()
                .id(1L)
                .title("TITLE")
                .count(1)
                .price(BigDecimal.TEN)
                .build();

        // Дефолтный мок для репозитория
        when(itemRepository.findById(anyLong())).thenReturn(Mono.just(item1));
        when(cartService.getCountForItem(anyLong(), any())).thenReturn(Mono.just(1));
    }

    @Test
    void getItemDtoByIdTest() {
        // when
        Mono<ItemDto> result = itemService.getItemDtoById(item1.getId(), session);

        // then
        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getId().equals(item1.getId()) &&
                                dto.getTitle().equals(item1.getTitle()) &&
                                dto.getCount() == 1
                )
                .verifyComplete();
    }

    @Test
    void getItemChunksTest() {
        String search = "";
        String sort = "NO";
        int pageSize = 6;
        int pageNumber = 1;
        int offset = 0;

        List<Item> items = List.of(
                Item.builder().id(1L).title("Item1").price(BigDecimal.valueOf(10)).build(),
                Item.builder().id(2L).title("Item2").price(BigDecimal.valueOf(20)).build(),
                Item.builder().id(3L).title("Item3").price(BigDecimal.valueOf(30)).build(),
                Item.builder().id(4L).title("Item4").price(BigDecimal.valueOf(40)).build()
        );

        when(itemRepository.searchItems(eq(search), eq(sort), eq(pageSize), eq(offset)))
                .thenReturn(Flux.fromIterable(items));
        for (Item item : items) {
            when(cartService.getCountForItem(eq(item.getId()), any())).thenReturn(Mono.just(1));
            when(itemRepository.findById(eq(item.getId()))).thenReturn(Mono.just(item));
        }

        Mono<List<List<ItemDto>>> result = itemService.getItemChunks(search, sort, pageSize, pageNumber, session);

        StepVerifier.create(result)
                .assertNext(chunks -> {
                    assert chunks.size() == 2;
                    assert chunks.get(0).size() == 3;
                    assert chunks.get(1).size() == 1;
                })
                .verifyComplete();
    }
}
