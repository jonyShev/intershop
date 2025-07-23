package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.CartAction;
import com.jonyshev.intershop.model.Item;
import com.jonyshev.intershop.repository.ReactiveItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final static String CART_KEY = "CART";
    private final ReactiveItemRepository itemRepository;

    @Override
    public void addItem(Long id, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.put(id, cart.getOrDefault(id, 0) + 1);
    }

    @Override
    public void decreaseItem(Long id, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.computeIfPresent(id, (itemId, count) -> (count > 1) ? count - 1 : null);
    }

    @Override
    public void deleteItem(Long id, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.remove(id);
    }

    @Override
    public Mono<List<Item>> getCartItems(WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        return Flux.fromIterable(cart.entrySet())
                .flatMap(entry -> {
                    Long id = entry.getKey();
                    return itemRepository.findById(id);
                })
                .collectList();
    }

    @Override
    public Mono<BigDecimal> getTotalPrice(WebSession session) {
        Map<Long, Integer> cart = getCart(session);

        return Flux.fromIterable(cart.entrySet())
                .flatMap(entry -> {
                    Long id = entry.getKey();
                    Integer count = entry.getValue();

                    return itemRepository.findById(id)
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(count)))
                            .switchIfEmpty(Mono.just(BigDecimal.ZERO));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isEmpty(WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        return cart.isEmpty();
    }

    @Override
    public Mono<Integer> getCountForItem(Long id, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        int count = cart.getOrDefault(id, 0);
        return Mono.just(count);
    }

    @Override
    public Mono<Void> clear(WebSession session) {
        getCart(session).clear();
        return Mono.empty();
    }

    @Override
    public Mono<Void> updateCartAction(Long id, CartAction action, WebSession session) {
        switch (action) {
            case PLUS -> addItem(id, session);
            case MINUS -> decreaseItem(id, session);
            case DELETE -> deleteItem(id, session);
        }
        return Mono.empty();
    }

    @Override
    public Mono<List<ItemDto>> getCartItemsDto(WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        return Flux.fromIterable(cart.entrySet())
                .flatMap(entry -> {
                    Long id = entry.getKey();
                    int count = entry.getValue();
                    return itemRepository.findById(id)
                            .map(item -> ItemDto.builder()
                                    .id(item.getId())
                                    .title(item.getTitle())
                                    .description(item.getDescription())
                                    .imgPath(item.getImgPath())
                                    .price(item.getPrice())
                                    .count(count)
                                    .build());
                })
                .collectList();
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(WebSession session) {
        return (Map<Long, Integer>) session.getAttributes().computeIfAbsent(CART_KEY, key -> new HashMap<Long, Integer>());
    }
}
