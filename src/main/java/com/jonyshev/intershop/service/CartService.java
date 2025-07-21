package com.jonyshev.intershop.service;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public interface CartService {

    /*void addItem(Long id);*/

    /*void decreaseItem(Long id);

    void deleteItem(Long id);

    List<ItemDto> getCartItemsDto();

    List<Item> getCartItems();

    BigDecimal getTotalPrice();

    boolean isEmpty();*/

    Mono<Integer> getCountForItem(Long id, WebSession session);

   /* void clear();

    void updateCartAction(Long id, CartAction action);*/

}
