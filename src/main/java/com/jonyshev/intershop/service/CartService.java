package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CartService {

    void addItem(Long id, WebSession session);

    void decreaseItem(Long id, WebSession session);

    void deleteItem(Long id, WebSession session);

    /*List<ItemDto> getCartItemsDto();

    List<Item> getCartItems();*/

    Mono<BigDecimal> getTotalPrice(WebSession session);

    boolean isEmpty(WebSession session);

    Mono<Integer> getCountForItem(Long id, WebSession session);

    /*void clear();*/

    Mono<Void> updateCartAction(Long id, CartAction action, WebSession session);

    Flux<ItemDto> getCartItemsDto(WebSession session);

}
