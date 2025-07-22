package com.jonyshev.intershop.service;

import com.jonyshev.intershop.dto.ItemDto;
import com.jonyshev.intershop.model.Item;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReactiveItemService {

    Flux<Item> getAllItems(String search, String sort, int pageSize, int pageNumber);

    List<List<ItemDto>> chunkItems(List<ItemDto> itemDtos, int i);

    Mono<ItemDto> mapToDto(Item item, WebSession session);

    Mono<ItemDto> getItemDtoById(Long id, WebSession session);

    Mono<List<List<ItemDto>>> getItemChunks(String search, String sort, int pageSize, int pageNumber, WebSession session);

}
