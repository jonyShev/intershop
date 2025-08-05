package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Item;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    void addItem(Long id, WebSession session);

    void decreaseItem(Long id, WebSession session);

    void deleteItem(Long id, WebSession session);

    Mono<List<Item>> getCartItems(WebSession session);

    Mono<BigDecimal> getTotalPrice(WebSession session);

    boolean isEmpty(WebSession session);

    Mono<Integer> getCountForItem(Long id, WebSession session);

    Mono<Void> clear(WebSession session);

    Mono<Void> updateCartAction(Long id, CartAction action, WebSession session);

    Mono<List<ItemDto>> getCartItemsDto(WebSession session);

}
