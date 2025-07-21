package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ReactiveItemService {

    Flux<Item> getAllItems(String search, String sort, int pageSize, int pageNumber);

    List<List<ItemDto>> chunkItems(List<ItemDto> itemDtos, int i);

    ItemDto mapToDto(Item item);
}
