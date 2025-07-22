package com.jonyshev.intershop.service;

import com.jonyshev.intershop.model.CartAction;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public interface CartService {

    void addItem(Long id, WebSession session);

    void decreaseItem(Long id, WebSession session);

    void deleteItem(Long id, WebSession session);

    /*List<ItemDto> getCartItemsDto();

    List<Item> getCartItems();

    BigDecimal getTotalPrice();

    boolean isEmpty();*/

    Mono<Integer> getCountForItem(Long id, WebSession session);

    /*void clear();*/

    Mono<Void> updateCartAction(Long id, CartAction action, WebSession session);

}
