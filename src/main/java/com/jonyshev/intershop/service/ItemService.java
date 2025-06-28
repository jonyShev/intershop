package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<Item> getAllItems();

    Optional<Item> getItemById(Long id);

    Item saveItem(Item item);

    void deleteItem(Long id);

    Page<Item> findItems(String search, String sort, Pageable pageable);

    List<ItemDto> mapToDto(List<Item> items, CartService cartService);

    ItemDto mapToDto(Item item, CartService cartService);
}
