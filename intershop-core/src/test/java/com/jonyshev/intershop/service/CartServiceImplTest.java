package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Spy
    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private WebSession session;

    private static final String USERNAME = "john";
    private static final String PRINCIPAL_KEY = "PRINCIPAL";
    private static String cartKey(String username) {
        return "CART:" + username;
    }

    @Test
    void getCartItemsTest() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        Map<Long, Integer> cart = new LinkedHashMap<>(); // чтобы был предсказуемый порядок
        cart.put(1L, 2);
        cart.put(2L, 1);
        attributes.put(PRINCIPAL_KEY, USERNAME);
        attributes.put(cartKey(USERNAME), cart);

        Item item1 = Item.builder().id(1L).title("Item 1").build();
        Item item2 = Item.builder().id(2L).title("Item 2").build();

        // mock
        when(session.getAttributes()).thenReturn(attributes);
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));

        // when
        Mono<List<Item>> result = cartService.getCartItems(session);

        // then
        StepVerifier.create(result)
                .assertNext(items -> {
                    // не завязываемся жёстко на порядок, но для LinkedHashMap всё равно будет [1,2]
                    var ids = items.stream().map(Item::getId).collect(Collectors.toList());
                    assert items.size() == 2;
                    assert ids.containsAll(List.of(1L, 2L));
                })
                .verifyComplete();
    }

    @Test
    void getTotalPriceTest() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 2);
        cart.put(2L, 1);
        attributes.put(PRINCIPAL_KEY, USERNAME);
        attributes.put(cartKey(USERNAME), cart);

        Item item1 = Item.builder().id(1L).price(BigDecimal.valueOf(10)).build(); // 2 * 10
        Item item2 = Item.builder().id(2L).price(BigDecimal.valueOf(30)).build(); // 1 * 30

        // mock
        when(session.getAttributes()).thenReturn(attributes);
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));

        // when
        Mono<BigDecimal> result = cartService.getTotalPrice(session);

        // then
        StepVerifier.create(result)
                .expectNext(BigDecimal.valueOf(50))
                .verifyComplete();
    }

    @Test
    void updateCartAction_shouldCallAddItem() {
        // given
        Long itemId = 1L;

        // mock internal method
        doNothing().when(cartService).addItem(itemId, session);

        // when
        cartService.updateCartAction(itemId, CartAction.PLUS, session).block();

        // then
        verify(cartService).addItem(itemId, session);
        verify(cartService, never()).decreaseItem(any(), any());
        verify(cartService, never()).deleteItem(any(), any());
    }

    @Test
    void updateCartAction_shouldCallDecreaseItem() {
        // given
        Long itemId = 2L;

        // mock internal method
        doNothing().when(cartService).decreaseItem(itemId, session);

        // when
        cartService.updateCartAction(itemId, CartAction.MINUS, session).block();

        // then
        verify(cartService).decreaseItem(itemId, session);
        verify(cartService, never()).addItem(any(), any());
        verify(cartService, never()).deleteItem(any(), any());
    }

    @Test
    void updateCartAction_shouldCallDeleteItem() {
        // given
        Long itemId = 3L;

        // mock internal method
        doNothing().when(cartService).deleteItem(itemId, session);

        // when
        cartService.updateCartAction(itemId, CartAction.DELETE, session).block();

        // then
        verify(cartService).deleteItem(itemId, session);
        verify(cartService, never()).addItem(any(), any());
        verify(cartService, never()).decreaseItem(any(), any());
    }
}

