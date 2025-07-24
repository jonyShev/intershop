package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private WebSession session;

    @Test
    void getItemDtoByIdTest() {
        //given
        Item item = Item.builder()
                .id(1L)
                .title("TITLE")
                .count(1)
                .build();

        //Mock
        when(itemRepository.findById(anyLong())).thenReturn(Mono.just(item));
        when(cartService.getCountForItem(anyLong(), any())).thenReturn(Mono.just(1));
        //when
        Mono<ItemDto> result = itemService.getItemDtoById(item.getId(), session);

        //then
        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getId().equals(1L) &&
                                dto.getTitle().equals("TITLE") &&
                                dto.getCount() == 1
                )
                .verifyComplete();
    }

    @Test
    void getItemChunksTest() {
        //given
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

        //Mock
        when(itemRepository.searchItems(eq(search), eq(sort), eq(pageSize), eq(offset)))
                .thenReturn(Flux.fromIterable(items));
        for (Item item : items) {
            when(cartService.getCountForItem(anyLong(), any())).thenReturn(Mono.just(1));
        }
        //when
        Mono<List<List<ItemDto>>> result = itemService.getItemChunks(search, sort, pageSize, pageNumber, session);

        //then
        StepVerifier.create(result)
                .assertNext(chunks -> {
                    assert chunks.size() == 2;
                    assert chunks.get(0).size() == 3;
                    assert chunks.get(1).size() == 1;
                })
                .verifyComplete();
    }
}
