package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemCacheDto;
import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    private ItemRepository itemRepository;
    private CartService cartService;
    private ItemCacheService itemCacheService;

    private ItemServiceImpl service;

    private WebSession session;

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        cartService = Mockito.mock(CartService.class);
        itemCacheService = Mockito.mock(ItemCacheService.class);
        service = new ItemServiceImpl(itemRepository, cartService, itemCacheService);
        session = new MockWebSession();
    }

    @Test
    void getAllItems_usesCorrectOffset() {
        int pageSize = 20;
        int pageNumber = 3; // offset = 40
        String search = "phone";
        String sort = "price,asc";

        Item i1 = mockItem(1L, "A", "D", "/a.png", new BigDecimal("10.00"));
        Item i2 = mockItem(2L, "B", "E", "/b.png", new BigDecimal("12.50"));

        when(itemRepository.searchItems(eq(search), eq(sort), eq(pageSize), eq(40)))
                .thenReturn(Flux.just(i1, i2));

        StepVerifier.create(service.getAllItems(search, sort, pageSize, pageNumber))
                .expectNext(i1, i2)
                .verifyComplete();

        verify(itemRepository).searchItems(search, sort, pageSize, 40);
    }

    @Test
    void chunkItems_splitsIntoRows() {
        List<ItemDto> flat = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            flat.add(ItemDto.builder().id((long) i).title("T" + i).price(new BigDecimal("1.00")).count(0).build());
        }
        List<List<ItemDto>> chunks = service.chunkItems(flat, 3);
        assertThat(chunks).hasSize(3);
        assertThat(chunks.get(0)).hasSize(3);
        assertThat(chunks.get(1)).hasSize(3);
        assertThat(chunks.get(2)).hasSize(1);
        assertThat(chunks.get(0).get(0).getId()).isEqualTo(0L);
        assertThat(chunks.get(2).get(0).getId()).isEqualTo(6L);
    }

    @Test
    void mapToDto_includesCartCount() {
        Item item = mockItem(10L, "Phone", "Desc", "/img.png", new BigDecimal("99.99"));
        when(cartService.getCountForItem(10L, session)).thenReturn(Mono.just(3));

        StepVerifier.create(service.mapToDto(item, session))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(10L);
                    assertThat(dto.getTitle()).isEqualTo("Phone");
                    assertThat(dto.getDescription()).isEqualTo("Desc");
                    assertThat(dto.getImgPath()).isEqualTo("/img.png");
                    assertThat(dto.getPrice()).isEqualByComparingTo("99.99");
                    assertThat(dto.getCount()).isEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    void getItemDtoById_cacheMiss_fetchDb_putToCache() {
        when(itemCacheService.getItem(7L)).thenReturn(Mono.empty());

        Item dbItem = mockItem(7L, "Mouse", "Nice", "/m.png", new BigDecimal("25.00"));
        when(itemRepository.findById(7L)).thenReturn(Mono.just(dbItem));
        when(itemCacheService.putItem(any())).thenReturn(Mono.empty());
        when(cartService.getCountForItem(7L, session)).thenReturn(Mono.just(5));

        StepVerifier.create(service.getItemDtoById(7L, session))
                .assertNext(dto -> {
                    assertThat(dto.getId()).isEqualTo(7L);
                    assertThat(dto.getTitle()).isEqualTo("Mouse");
                    assertThat(dto.getCount()).isEqualTo(5);
                })
                .verifyComplete();

        ArgumentCaptor<ItemCacheDto> captor = ArgumentCaptor.forClass(ItemCacheDto.class);
        verify(itemCacheService).putItem(captor.capture());
        ItemCacheDto put = captor.getValue();
        assertThat(put.id()).isEqualTo(7L);
        assertThat(put.title()).isEqualTo("Mouse");
        assertThat(put.price()).isEqualByComparingTo("25.00");

        InOrder inOrder = inOrder(itemCacheService, itemRepository);
        inOrder.verify(itemCacheService).getItem(7L);
        inOrder.verify(itemRepository).findById(7L);
    }

    @Test
    void getItemDtoById_notFound_throws() {
        when(itemCacheService.getItem(404L)).thenReturn(Mono.empty());
        when(itemRepository.findById(404L)).thenReturn(Mono.empty());

        StepVerifier.create(service.getItemDtoById(404L, session))
                .expectErrorSatisfies(th -> {
                    assertThat(th)
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessageContaining("Item not found: 404");
                })
                .verify();

        verify(itemCacheService, never()).putItem(any());
    }

    @Test
    void getItemChunks_cacheMiss_buildFromDb() {
        when(itemCacheService.getCatalogPage(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(Mono.empty());
        when(itemCacheService.putCatalogPage(anyString(), anyString(), anyInt(), anyInt(), anyList()))
                .thenReturn(Mono.empty());

        Item i1 = mockItem(1L, "A", "D", "/a.png", new BigDecimal("10.00"));
        Item i2 = mockItem(2L, "B", "E", "/b.png", new BigDecimal("20.00"));
        Item i3 = mockItem(3L, "C", "F", "/c.png", new BigDecimal("30.00"));
        Item i4 = mockItem(4L, "D", "G", "/d.png", new BigDecimal("40.00"));
        when(itemRepository.searchItems(anyString(), anyString(), eq(4), eq(0)))
                .thenReturn(Flux.just(i1, i2, i3, i4));

        when(cartService.getCountForItem(anyLong(), eq(session)))
                .thenReturn(Mono.just(1));

        StepVerifier.create(service.getItemChunks("q", "price,desc", 4, 1, session))
                .assertNext(rows -> {
                    assertThat(rows).hasSize(2); // 4 элемента -> по 3 в первой строке и 1 во второй
                    assertThat(rows.get(0)).hasSize(3);
                    assertThat(rows.get(1)).hasSize(1);
                    assertThat(rows.get(0).get(0).getId()).isEqualTo(1L);
                    assertThat(rows.get(0).get(0).getCount()).isEqualTo(1);
                })
                .verifyComplete();

        verify(itemCacheService).putCatalogPage(anyString(), anyString(), anyInt(), anyInt(), anyList());
    }

    private Item mockItem(Long id, String title, String desc, String img, BigDecimal price) {
        Item item = Mockito.mock(Item.class);
        when(item.getId()).thenReturn(id);
        when(item.getTitle()).thenReturn(title);
        when(item.getDescription()).thenReturn(desc);
        when(item.getImgPath()).thenReturn(img);
        when(item.getPrice()).thenReturn(price);
        return item;
    }
}
