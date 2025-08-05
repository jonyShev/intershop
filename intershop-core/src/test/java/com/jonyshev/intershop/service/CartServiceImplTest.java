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
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Spy
    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private WebSession session;

    @Test
    void getCartItemsTest() {
        //given
        HashMap<String, Object> attributes = new HashMap<>();
        HashMap<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 2);
        cart.put(2L, 1);
        attributes.put("CART", cart);
        Item item1 = Item.builder().id(1L).title("Item 1").build();
        Item item2 = Item.builder().id(2L).title("Item 2").build();
        //mock
        when(session.getAttributes()).thenReturn(attributes);
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));
        //when
        Mono<List<Item>> result = cartService.getCartItems(session);
        //then
        StepVerifier.create(result)
                .assertNext(items -> {
                    assert items.size() == 2;
                    assert items.get(0).getId().equals(1L);
                    assert items.get(1).getId().equals(2L);
                })
                .verifyComplete();
    }

    @Test
    void getTotalPriceTest(){
        //given
        HashMap<String, Object> attributes = new HashMap<>();
        HashMap<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 2);
        cart.put(2L, 1);
        attributes.put("CART", cart);
        Item item1 = Item.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        Item item2 = Item.builder().id(2L).price(BigDecimal.valueOf(30)).build();
        //mock
        when(session.getAttributes()).thenReturn(attributes);
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(itemRepository.findById(2L)).thenReturn(Mono.just(item2));
        //when
        Mono<BigDecimal> result = cartService.getTotalPrice(session);

        //then
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
        Long itemId = 2L;
        doNothing().when(cartService).decreaseItem(itemId, session);
        cartService.updateCartAction(itemId, CartAction.MINUS, session).block();
        verify(cartService).decreaseItem(itemId, session);
        verify(cartService, never()).addItem(any(), any());
        verify(cartService, never()).deleteItem(any(), any());
    }

    @Test
    void updateCartAction_shouldCallDeleteItem() {
        Long itemId = 3L;
        doNothing().when(cartService).deleteItem(itemId, session);
        cartService.updateCartAction(itemId, CartAction.DELETE, session).block();
        verify(cartService).deleteItem(itemId, session);
        verify(cartService, never()).addItem(any(), any());
        verify(cartService, never()).decreaseItem(any(), any());
    }
}
