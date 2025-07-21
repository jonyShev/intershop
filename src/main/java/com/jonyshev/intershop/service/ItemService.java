/*
package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Optional<Item> getItemById(Long id);

    Page<Item> findItems(String search, String sort, Pageable pageable);

    List<ItemDto> mapToDto(List<Item> items, CartService cartService);

    ItemDto mapToDto(Item item, CartService cartService);

    List<List<ItemDto>> chunkItems(List<ItemDto> itemDtos, int i);

    Sort getSort(String sort);

    Pageable buildPageable(int pageNumber, int pageSize, String sort);
}
*/
